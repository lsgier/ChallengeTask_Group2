package challengetask.group02.controllers;

import net.tomp2p.peers.Number160;

public class TreeControllerHashtableChildren implements TreeControllerStrategy {


    //TODO should this implementation or the context class handle the current directory?
    private String currentDirectory;
    private Number160 currentDirectoryID;

    public Number160 findEntry(String path) {
        return null;
    }

    public String getPath(Number160 EntryID) {
        return null;
    }

    public void createDir(String path) {

    }
}
