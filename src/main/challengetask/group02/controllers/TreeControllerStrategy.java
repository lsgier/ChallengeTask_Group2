package challengetask.group02.controllers;

import net.tomp2p.peers.Number160;

public interface TreeControllerStrategy {

    //traverses the given path to get to the leaf-object of the path
    Number160 findEntry(String path);

    //the reverse of findEntry
    String getPath(Number160 EntryID);

    //if path is relative, concatenate current directory
    void createDir(String path);


    

}
