package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

import static challengetask.group02.fsstructure.Entry.TYPE.DIRECTORY;
import static challengetask.group02.fsstructure.Entry.TYPE.FILE;

public class TreeControllerHashtableChildren implements TreeControllerStrategy {

    PeerDHT peer;

    public TreeControllerHashtableChildren(PeerDHT peer) {
        this.peer = peer;
    }

    public Entry getEntryFromID(Number160 ID) throws IOException, ClassNotFoundException {
        //TODO vDHT
        /*TODO things that can go wrong here
        * (basic DHT stuff; put and get)
        * fs errors:
        * - no result->why was this referenced
        * - child name not found
        *
        *
        * */

        if (ID == null) {
            System.err.println("BUG: trying to get Entry with ID null!");
            return new Directory(null, null, "dummy");
        }
        FutureGet futureGet = peer.get(ID).start();
        futureGet.awaitUninterruptibly();
        return (Entry) futureGet.data().object();
    }

    private void putNewEntry(Entry entry) throws IOException {
        FuturePut futurePut = peer.put(entry.getID()).data(new Data(entry)).start();
        futurePut.awaitUninterruptibly();
    }

    private void linkChildToParent(Directory parent, Entry child) throws IOException {
        //TODO vDHT
        //TODO QUESTION asynchronous?
        parent.addChild(child.getEntryName(), child.getID(), child.getType());
        FuturePut futurePut = peer.put(parent.getID()).data(new Data(parent)).start();
        futurePut.awaitUninterruptibly();
    }

    @Override
    public Entry findEntry(String path) throws IOException, ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException {
        Path subPaths = Paths.get(path);

        //TODO IDEA could another controller implementation use a cache or something? it could remember the last used path.
        //->typically multiple operations happen in the same directory.

        //first, get the root directory

        //TODO QUESTION ask user to create root node if no root is found? this way the system stays usable if the root gets broken.
        //RESOLVED? QUESTION create root if root is not found?
        //->no; root node is created during the first bootstrap

        Directory currentDirectory = (Directory) getEntryFromID(Number160.ZERO);

        Number160 currentChildFile;
        Number160 currentChildDir;

        for (Path dir : subPaths) {
            currentChildFile = currentDirectory.getChild(dir.toString(), FILE);
            currentChildDir = currentDirectory.getChild(dir.toString(), DIRECTORY);

            if (currentChildFile != null && subPaths.endsWith(dir)) {
                return getEntryFromID(currentDirectory.getChild(dir.toString(), FILE));
            }
            if (currentChildFile != null && !subPaths.endsWith(dir)) {
                throw new NotADirectoryException("");
            }
            if (currentChildDir == null) {
                throw new NoSuchFileOrDirectoryException(dir.toString());
            } else {
                currentDirectory = (Directory) getEntryFromID(currentDirectory.getChild(dir.toString(), DIRECTORY));
            }
        }

        return currentDirectory;
    }

    @Override
    public String getPath(Number160 EntryID) {
        return null;
    }

    @Override
    public void createDir(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException {
        Path subPaths = Paths.get(path);
        int pathLength = subPaths.getNameCount();
        if (pathLength == 0) {
            throw new NotADirectoryException("Don't create a root node like that! Path: "+path);
        }

        Number160 newKey = Number160.createHash(UUID.randomUUID().hashCode());

        Entry parentEntry = findEntry(subPaths.getParent().toString());
        if (parentEntry.getType() == FILE) {
            throw new NotADirectoryException(subPaths.getParent().toString());
        }

        Directory newDir = new Directory(newKey, parentEntry.getID(), subPaths.getFileName().toString());
        putNewEntry(newDir);
        linkChildToParent((Directory) parentEntry, newDir);
    }

    @Override
    public ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException {

        Entry result = findEntry(path);
        if (result.getType() == FILE) {
            throw new NotADirectoryException(path);
        }
        Directory dir = (Directory) result;
        Hashtable<String, Number160> children = dir.getChildren(FILE);
        children.putAll(dir.getChildren(DIRECTORY));

        return new ArrayList<String>(children.keySet());
    }

    @Override
    public void renameEntry(String from, String to) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException {
        //TODO vDHT

        Path oldPath = Paths.get(from);
        Path newPath = Paths.get(to);

        Entry entry = findEntry(from);

        String oldName = oldPath.getFileName().toString();
        String newName = newPath.getFileName().toString();

        //if path different
        //add new link to new parent
        //remove link from old parent

        if (oldPath.getParent().compareTo(newPath.getParent()) == 0) {
            //Modify entry name
            entry.setEntryName(newName);
            putNewEntry(entry);

            //Modify parent
            Directory parent = (Directory) getEntryFromID(entry.getParentID());
            parent.renameChild(oldName, newName);
            putNewEntry(parent);
        } else {
            Directory oldParent = (Directory) findEntry(oldPath.getParent().toString());
            Directory newParent = (Directory) findEntry(newPath.getParent().toString());

            newParent.addChild(newName, entry.getID(), entry.getType());
            oldParent.removeChild(oldName);

            entry.setEntryName(newName);

            putNewEntry(oldParent);
            putNewEntry(newParent);
            putNewEntry(entry);
        }
    }
}
