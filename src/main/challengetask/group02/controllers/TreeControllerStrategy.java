package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Entry;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.util.ArrayList;

public interface TreeControllerStrategy {

    //traverses the given path to get to the leaf-object of the path
    Entry findEntry(String path) throws IOException, ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException;

    //the reverse of findEntry
    String getPath(Number160 EntryID);

    void createDir(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException;

    //returns just the list with the names of the files and directories in that path
    ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException;

    //returns





    

}
