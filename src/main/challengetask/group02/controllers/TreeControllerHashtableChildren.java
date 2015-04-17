package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fsstructure.File;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;

import static challengetask.group02.fsstructure.Entry.TYPE.DIRECTORY;
import static challengetask.group02.fsstructure.Entry.TYPE.FILE;

public class TreeControllerHashtableChildren implements TreeControllerStrategy {

    PeerDHT peer;

    public TreeControllerHashtableChildren(PeerDHT peer) {
        this.peer = peer;
    }

    private String currentDirectory;
    private Number160 currentDirectoryID;
    //TODO implement random number
    private int random = 0;

    private enum ACTIONTYPE {
        NEWENTRY,
        ADDCHILD,
        RENAME;
    }

    private Entry getEntryFromID(Number160 ID) throws IOException, ClassNotFoundException {
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

    private void consistencySafeTreeModifier(Entry entry, ACTIONTYPE action) {

    }

    public Entry findEntry(String path) throws IOException, ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException {
        Path subPaths = Paths.get(path);

        //TODO IDEA could another controller implementation use a cache or something? it could remember the last used path.
        //->typically multiple operations happen in the same directory.

        //first, get the root directory
        //TODO QUESTION create root if root is not found?
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

    public String getPath(Number160 EntryID) {
        return null;
    }

    public void createDir(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException {
        Path subPaths = Paths.get(path);
        int pathlength = subPaths.getNameCount();
        Path parentPath= subPaths.subpath(0, pathlength - 2);

        Number160 newKey = Number160.createHash(random);



        Entry parentEntry = findEntry(parentPath.toString());
        if (parentEntry.getType() == FILE) {
            throw new NotADirectoryException(parentPath.toString());
        }

        Directory newDir = new Directory(newKey, parentEntry.getID(), subPaths.getName(pathlength-1).toString());

        //TODO upload newDir
        //TODO link newDir to parent



    }

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


}
