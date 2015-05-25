package challengetask.group02.controllers;

import challengetask.group02.controllers.exceptions.*;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fsstructure.File;
import challengetask.group02.helpers.DHTPutGetHelper;
import net.fusejna.StructStat;
import net.fusejna.types.TypeMode;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

import static challengetask.group02.fsstructure.Entry.TYPE.DIRECTORY;
import static challengetask.group02.fsstructure.Entry.TYPE.FILE;

public class TreeController implements ITreeController {

    PeerDHT peer;

    public TreeController(PeerDHT peer) {
        this.peer = peer;
    }

    private Entry getEntryFromID(Number160 ID) throws IOException, ClassNotFoundException, NoSuchFileOrDirectoryException {

        if (ID == null) {
            System.err.println("BUG: trying to get Entry with ID null!");
            throw new NoSuchFileOrDirectoryException("Tried to get entry with ID null.");
        }

        FutureGet futureGet = peer.get(ID).start();
        futureGet.awaitUninterruptibly();
        if (futureGet.isEmpty()) {
            System.out.println("getEntryFromID did not get a result -> faulty fs");
            throw new NoSuchFileOrDirectoryException("");
        }
        return (Entry) futureGet.data().object();
    }

    @Override
    public Entry findEntry(String path) throws IOException, ClassNotFoundException, FsException {
        Path subPaths = Paths.get(path);

        //TODO IDEA the cache should be implemented here

        //first, get the root directory

        Directory currentDirectory;

        try {
            currentDirectory = (Directory) getEntryFromID(Number160.ZERO);
        } catch (NoSuchFileOrDirectoryException e) {
            throw new FsException("No root node was found. Probably not connected to a P2P network with a running file system.");
        }


        Number160 currentChildFileID;
        Number160 currentChildDirID;

        for (Path dir : subPaths) {
            //try to get the next child ID from the current directory. If the path is correct one will assigned with an ID and one with null.
            currentChildFileID = currentDirectory.getChild(dir.toString(), FILE);
            currentChildDirID = currentDirectory.getChild(dir.toString(), DIRECTORY);

            if (currentChildFileID != null && subPaths.endsWith(dir)) {
                return getEntryFromID(currentChildFileID);
            }
            else if (currentChildFileID != null && !subPaths.endsWith(dir)) {
                throw new NotADirectoryException("");
            }
            else if (currentChildDirID == null) {
                throw new NoSuchFileOrDirectoryException(dir.toString());
            } else {
                currentDirectory = (Directory) getEntryFromID(currentChildDirID);
            }
        }

        return currentDirectory;
    }

    @Override
    public File getFile(String path) throws ClassNotFoundException, FsException, IOException {
        Entry entry = findEntry(path);
        if (entry.getType() == FILE) {
            return (File) entry;
        } else {
            throw new NotAFileException(path);
        }
    }
    @Override
    public Directory getDirectory(String path) throws ClassNotFoundException, FsException, IOException {
        Entry entry = findEntry(path);
        if (entry.getType() == DIRECTORY) {
            return (Directory) entry;
        } else {
            throw new NotADirectoryException(path);
        }
    }

    @Override
    public String getPath(Number160 EntryID) {
        return null;
    }

    @Override
    public void createDir(String path) throws ClassNotFoundException, FsException, IOException {
        Path subPaths = Paths.get(path);
        int pathLength = subPaths.getNameCount();
        if (pathLength == 0) {
            throw new NotADirectoryException("Don't create a root node like that! Path: "+path);
        }

        Number160 newKey = Number160.createHash(UUID.randomUUID().hashCode());

        Directory parentEntry = getDirectory(subPaths.getParent().toString());

        Directory newDir = new Directory(newKey, parentEntry.getID(), subPaths.getFileName().toString());

        DHTPutGetHelper helper = new DHTPutGetHelper(peer);
        helper.addNewEntry(parentEntry, newDir);
    }

    @Override
    public void createFile(String path) throws ClassNotFoundException, FsException, IOException {

        Path subPaths = Paths.get(path);
        
        int pathLength = subPaths.getNameCount();
        if (pathLength == 0) {
            throw new NoSuchFileOrDirectoryException("Can not create such file");
        }

        Number160 newKey = Number160.createHash(UUID.randomUUID().hashCode());

        Directory parentEntry = getDirectory(subPaths.getParent().toString());

        File newFile = new File (newKey, parentEntry.getID(), subPaths.getFileName().toString());

        //this is new locking logic, due to fuse constraints we have to associate a file creation with the respective owner
        newFile.setDirtyBit(true);
        newFile.setModifierPeer(peer.peerID());
        
        DHTPutGetHelper helper = new DHTPutGetHelper(peer);
        helper.addNewEntry(parentEntry, newFile);
    }

    @Override
    public ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException, FsException {

        Directory dir = getDirectory(path);

        Hashtable<String, Number160> children = dir.getChildren(FILE);
        children.putAll(dir.getChildren(DIRECTORY));

        return new ArrayList<>(children.keySet());
    }

    @Override
    public void renameEntry(String from, String to) throws ClassNotFoundException, FsException, IOException {
        Path oldPath = Paths.get(from);
        Path newPath = Paths.get(to);

        Entry entry = findEntry(from);

        String newName = newPath.getFileName().toString();

        //if path different
        //add new link to new parent
        //remove link from old parent
        DHTPutGetHelper helper = new DHTPutGetHelper(peer);
        if (oldPath.getParent().compareTo(newPath.getParent()) == 0) {

            Directory parent = getDirectory(oldPath.getParent().toString());
            helper.updateEntryName(parent, entry, newName);

        } else {
            Directory oldParent = getDirectory(oldPath.getParent().toString());
            Directory newParent = getDirectory(newPath.getParent().toString());

            helper.moveEntry(newParent, oldParent, entry, newName);
        }

    }

    @Override
    public void removeDirectory(String path) throws IOException, ClassNotFoundException, FsException {
        Path dirPath = Paths.get(path);
        Directory dirEntry = getDirectory(path);
        Directory parentEntry = getDirectory(dirPath.getParent().toString());

        if (dirEntry.getChildren().isEmpty()) {
            DHTPutGetHelper helper = new DHTPutGetHelper(peer);
            helper.removeAndDeleteChild(parentEntry, dirEntry);
        } else {
            throw new DirectoryNotEmptyException(path);
        }
    }

    @Override
    public void deleteFile(String path) throws ClassNotFoundException, FsException, IOException {
        Path dirPath = Paths.get(path);
        File file = getFile(path);
        Directory parent = getDirectory(dirPath.getParent().toString());

        DHTPutGetHelper helper = new DHTPutGetHelper(peer);
        helper.clearAndDeleteFile(file);
        helper.removeAndDeleteChild(parent, file);
    }

    //used for the locking logic
    @Override
    public void whenFileClosed(String path) throws ClassNotFoundException, FsException, IOException {
        File file = getFile(path);
        DHTPutGetHelper helper = new DHTPutGetHelper(peer);
        helper.flushFile(file);
    }

    @Override
    public void updateFileMetaData(String path, final StructStat.StatWrapper stat) throws ClassNotFoundException, FsException, IOException {
        Entry entry = findEntry(path);
        if (entry.getType() == Entry.TYPE.DIRECTORY) {
            stat.setMode(TypeMode.NodeType.DIRECTORY);
        }
        if (entry.getType() == Entry.TYPE.FILE) {
            stat.setMode(TypeMode.NodeType.FILE);
            File file = (File) entry;
            stat.setMode(TypeMode.NodeType.FILE).size(entry.getSize());
            stat.atime(file.getAtime());
            stat.ctime(file.getCtime());
        }
    }

}
