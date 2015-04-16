package challengetask.group02.controllers;

import net.tomp2p.dht.PeerDHT;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The FS operations handled by the controller are implemented in the strategy pattern. See github wiki for explanation:
 * https://github.com/lisgie/ChallengeTask_Group2/wiki/Architecture
 *
 *
 */
public class TreeControllerContext {

    TreeControllerStrategy controller;

    public TreeControllerContext(PeerDHT peer) {
        this.controller = new TreeControllerHashtableChildren(peer);
    }

    public ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException {
        return controller.readDir(path);
    }

    //TODO test creating a root directory object "/" and some other directories

    //TODO moving the other directories into the root directory

    //TODO test listing the content of root "/" and assert if added directories are in it

    //TODO test requesting a certain subdirectory of root "/"

    //TODO test what happens if a nonexisting subdirectory is requested
}
