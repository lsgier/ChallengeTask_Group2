package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fsstructure.File;
import net.fusejna.StructStat;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ControllerContext {

    private final FileContentController fileContentController;
    private final TreeControllerStrategy treeController;


    public ControllerContext(PeerDHT peer) {
        this.treeController = new TreeController(peer);
        this.fileContentController = new FileContentController(peer);
    }

    public ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException {
        return treeController.readDir(path);
    }

    public Entry findEntry(String path) throws IOException, ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException {
        return treeController.findEntry(path);
    }

    public void createDir(String path) throws ClassNotFoundException, NotADirectoryException, IOException, NoSuchFileOrDirectoryException {
        treeController.createDir(path);
    }

    public void createFile(String path) throws ClassNotFoundException, NotADirectoryException, IOException, NoSuchFileOrDirectoryException {
        this.treeController.createFile(path);
    }

    public void rename(String path, String newName) throws ClassNotFoundException, NotADirectoryException, IOException, NoSuchFileOrDirectoryException {
        treeController.renameEntry(path, newName);
    }

    public void deleteDirectory(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException, DirectoryNotEmptyException {
        treeController.removeDirectory(path);
    }

    /**
     * This method first clears the file (deletes all blocks) and then unlinks and deletes the file object.
     * @param path
     * @throws ClassNotFoundException
     * @throws NotADirectoryException
     * @throws NoSuchFileOrDirectoryException
     * @throws NotAFileException
     * @throws IOException
     */
    public void deleteFile(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, NotAFileException, IOException {
        treeController.deleteFile(path);
    }

    public byte[] readFile(String path, long size, long offset) throws ClassNotFoundException, NotADirectoryException, NotAFileException, IOException, NoSuchFileOrDirectoryException, CRCException {
        File file = treeController.getFile(path);
        return this.fileContentController.readFile(file, size, offset);
    }

    public int writeFile(String path, ByteBuffer buf, long bufSize, long writeOffset) throws ClassNotFoundException, NotADirectoryException, NotAFileException, IOException, NoSuchFileOrDirectoryException, BusyException {
        File file = treeController.getFile(path);
        return this.fileContentController.writeFile(file, buf, bufSize, writeOffset);
    }

    //used for the locking logic
	public void whenFileClosed(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, NotAFileException, IOException {
		treeController.whenFileClosed(path);
	}
	
	public void updateFileMetaData(String path, final StructStat.StatWrapper stat) throws ClassNotFoundException, NotADirectoryException, IOException, NoSuchFileOrDirectoryException {
		treeController.updateFileMetaData(path, stat);
	}
}
