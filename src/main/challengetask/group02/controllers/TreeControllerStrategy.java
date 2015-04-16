package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Entry;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.util.ArrayList;

public interface TreeControllerStrategy {

    //traverses the given path to get to the leaf-object of the path
    Entry findEntry(String path) throws IOException, ClassNotFoundException;

    //the reverse of findEntry
    String getPath(Number160 EntryID);

    //if path is relative, concatenate current directory
    void createDir(String path);

    //returns just the list with the names of the files and directories in that path
    ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException;

    //returns





    

}
