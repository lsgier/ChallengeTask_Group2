package challengetask.group02.controllers;

import challengetask.group02.controllers.exceptions.*;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fsstructure.File;
import challengetask.group02.helpers.SimpleCache;
import net.fusejna.StructStat;
import net.tomp2p.dht.PeerDHT;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ControllerContext {

    SimpleCache<File> cache = new SimpleCache<>(1);

    private final IFileContentController fileContentController;
    private final ITreeController treeController;

    public ControllerContext(PeerDHT peer) {
        this.treeController = new TreeController(peer);
        this.fileContentController = new FileContentController(peer);
    }

    public ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException, FsException {
        return treeController.readDir(path);
    }

    public void createDir(String path) throws ClassNotFoundException, FsException, IOException {
        treeController.createDir(path);
    }

    public void createFile(String path) throws ClassNotFoundException, FsException, IOException {
        this.treeController.createFile(path);
    }

    public void rename(String path, String newName) throws ClassNotFoundException, FsException, IOException {
        treeController.renameEntry(path, newName);
    }

    public void deleteDirectory(String path) throws ClassNotFoundException, FsException, IOException {
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
    public void deleteFile(String path) throws ClassNotFoundException, FsException, IOException {
        treeController.deleteFile(path);
    }

    public byte[] readFile(String path, long size, long offset) throws ClassNotFoundException, FsException, IOException, CRCException {
        File file = treeController.getFile(path);
        return this.fileContentController.readFile(file, size, offset);
    }

    public int writeFile(String path, ByteBuffer buf, long bufSize, long writeOffset) throws ClassNotFoundException, FsException, IOException {
        if (cache.get(path) != null){
            return this.fileContentController.writeFile(cache.get(path), buf, bufSize, writeOffset, cache);
        } else {
            File file = treeController.getFile(path);
            cache.put(path, file);
            return this.fileContentController.writeFile(file, buf, bufSize, writeOffset, cache);
        }
    }

    //used for the locking logic
	public void whenFileClosed(String path) throws ClassNotFoundException, FsException, IOException {
		treeController.whenFileClosed(path);
        fileContentController.flush(path, treeController.getFile(path));
	}
	
	public void updateFileMetaData(String path, final StructStat.StatWrapper stat) throws ClassNotFoundException, FsException, IOException {
		treeController.updateFileMetaData(path, stat);
	}
}
