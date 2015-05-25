package challengetask.group02.controllers;

import challengetask.group02.controllers.exceptions.FsException;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fsstructure.File;
import net.fusejna.StructStat;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.util.ArrayList;

public interface ITreeController {

    //traverses the given path to get to the leaf-object of the path
    Entry resolvePath(String path) throws IOException, ClassNotFoundException, FsException;

    File getFile(String path) throws ClassNotFoundException, FsException, IOException;

    Directory getDirectory(String path) throws ClassNotFoundException, FsException, IOException;

    void createDir(String path) throws ClassNotFoundException, FsException, IOException;

    //returns just the list with the names of the files and directories in that path
    ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException, FsException;

    void renameEntry(String path, String newName) throws ClassNotFoundException, FsException, IOException;

    void createFile(String path) throws ClassNotFoundException, FsException, IOException;

    void deleteFile(String path) throws ClassNotFoundException, FsException, IOException;

    void whenFileClosed(String path) throws ClassNotFoundException, FsException, IOException;

    void removeDirectory(String path) throws IOException, ClassNotFoundException, FsException;

    void updateFileMetaData(String path, StructStat.StatWrapper stat) throws ClassNotFoundException, FsException, IOException;
}
