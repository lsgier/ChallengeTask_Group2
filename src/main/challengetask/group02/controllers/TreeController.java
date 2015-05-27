package challengetask.group02.controllers;

import challengetask.group02.controllers.exceptions.*;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fsstructure.File;
import challengetask.group02.helpers.FSModifyHelper;
import challengetask.group02.helpers.PathResolver;

import net.fusejna.StructStat;
import net.fusejna.types.TypeMode;

import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import static challengetask.group02.fsstructure.Entry.TYPE.DIRECTORY;
import static challengetask.group02.fsstructure.Entry.TYPE.FILE;

public class TreeController implements ITreeController {

    private final FSModifyHelper helper;
    private PeerDHT peer;

    public TreeController(PeerDHT peer) {
        this.peer = peer;
        helper = new FSModifyHelper(peer);
    }

    //methods to view and traverse the tree

    @Override
    public Entry resolvePath(String path) throws IOException, ClassNotFoundException, FsException {
        return PathResolver.resolvePath(path, helper);
    }

    @Override
    public File getFile(String path) throws ClassNotFoundException, FsException, IOException {
        Entry entry = resolvePath(path);
        if (entry.getType() == FILE) {
            return (File) entry;
        } else {
            throw new NotAFileException(path);
        }
    }
    @Override
    public Directory getDirectory(String path) throws ClassNotFoundException, FsException, IOException {
        Entry entry = resolvePath(path);
        if (entry.getType() == DIRECTORY) {
            return (Directory) entry;
        } else {
            throw new NotADirectoryException(path);
        }
    }

    @Override
    public ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException, FsException {

        Directory dir = getDirectory(path);

        return new ArrayList<>(dir.getChildren().keySet());

        //Hashtable<String, Number160> children = dir.getChildren(FILE);
        //children.putAll(dir.getChildren(DIRECTORY));

        //return new ArrayList<>(children.keySet());
    }

    //methods to modify the tree

    @Override
    public void createDir(String path) throws ClassNotFoundException, FsException, IOException {
        Path subPaths = Paths.get(path);
        int pathLength = subPaths.getNameCount();
        if (pathLength == 0) {
            throw new NotADirectoryException("Don't create a root node like that! Path: "+path);
        }
        Directory parentEntry = getDirectory(subPaths.getParent().toString());

        if (parentEntry.getChild(subPaths.getFileName().toString()) != null) {
            throw new FileExistsException(path);
        }

        Number160 newKey = Number160.createHash(UUID.randomUUID().hashCode());
        Directory newDir = new Directory(newKey, subPaths.getFileName().toString());

        helper.addNewEntry(parentEntry, newDir);
    }

    @Override
    public void createFile(String path) throws ClassNotFoundException, FsException, IOException {

        Path subPaths = Paths.get(path);

        int pathLength = subPaths.getNameCount();
        if (pathLength == 0) {
            throw new NoSuchFileOrDirectoryException("Can not create such file");
        }

        Directory parentEntry = getDirectory(subPaths.getParent().toString());
        if (parentEntry.getChild(subPaths.getFileName().toString()) != null) {
            throw new FileExistsException(path);
        }


        Number160 newKey = Number160.createHash(UUID.randomUUID().hashCode());
        File newFile = new File (newKey, subPaths.getFileName().toString());

        //this is new locking logic, due to fuse constraints we have to associate a file creation with the respective owner
        newFile.setDirtyBit(true);
        newFile.setModifierPeer(peer.peerID());


        helper.addNewEntry(parentEntry, newFile);
    }

    @Override
    public void renameEntry(String from, String to) throws ClassNotFoundException, FsException, IOException {
        Path oldPath = Paths.get(from);
        Path newPath = Paths.get(to);

        Entry entry = resolvePath(from);

        String newName = newPath.getFileName().toString();

        //if the path is the same, just rename the entry
        //if the path is different
        //add new link to new parent
        //remove link from old parent

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
            helper.removeEntry(parentEntry, dirEntry);
        } else {
            throw new DirectoryNotEmptyException(path);
        }
    }

    @Override
    public void deleteFile(String path) throws ClassNotFoundException, FsException, IOException {
        Path dirPath = Paths.get(path);
        File file = getFile(path);
        Directory parent = getDirectory(dirPath.getParent().toString());


        helper.clearAndDeleteFile(file);
        helper.removeEntry(parent, file);
    }

    //used for the locking logic
    @Override
    public void whenFileClosed(String path) throws ClassNotFoundException, FsException, IOException {
        File file = getFile(path);

        helper.flushFile(file);
    }

    @Override
    public void updateFileMetaData(String path, final StructStat.StatWrapper stat) throws ClassNotFoundException, FsException, IOException {
        Entry entry = resolvePath(path);
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
