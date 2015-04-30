package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fsstructure.File;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.util.ArrayList;

public interface TreeControllerStrategy {

    Entry getEntryFromID(Number160 ID) throws IOException, ClassNotFoundException;

    //traverses the given path to get to the leaf-object of the path
    Entry findEntry(String path) throws IOException, ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException;

    File getFile(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException, NotAFileException;

    Directory getDirectory(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException, NotAFileException;

    //the reverse of findEntry
    String getPath(Number160 EntryID);

    void createDir(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException;

    //returns just the list with the names of the files and directories in that path
    ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException;

    void renameEntry(String path, String newName) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException;

    void createFile(String path) throws ClassNotFoundException, NotADirectoryException, NoSuchFileOrDirectoryException, IOException;
}
