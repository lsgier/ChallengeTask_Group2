package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Entry;
import net.tomp2p.dht.PeerDHT;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The FS operations handled by the controller are implemented in the strategy pattern. See github wiki for explanation:
 * https://github.com/lisgie/ChallengeTask_Group2/wiki/Architecture
 *
 *
 */
public class ControllerContext {

    TreeControllerStrategy treeController;


    public ControllerContext(PeerDHT peer) {
        this.treeController = new TreeControllerHashtableChildren(peer);
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

    public String getDefaultFileContent() {
        return null;
    }

    public void createFile(String path) {}


    public void rename(String path, String newName) throws ClassNotFoundException, NotADirectoryException, IOException, NoSuchFileOrDirectoryException {
        treeController.renameEntry(path, newName);
    }

    public void deleteDirectory(String path) {}

    public void deleteFile(String path) {}

    //TODO test creating a root directory object "/" and some other directories

    //TODO moving the other directories into the root directory

    //TODO test listing the content of root "/" and assert if added directories are in it

    //TODO test requesting a certain subdirectory of root "/"

    //TODO test what happens if a nonexisting subdirectory is requested
}
