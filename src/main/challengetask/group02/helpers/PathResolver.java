package challengetask.group02.helpers;


import challengetask.group02.controllers.exceptions.FsException;
import challengetask.group02.controllers.exceptions.NoSuchFileOrDirectoryException;
import challengetask.group02.controllers.exceptions.NotADirectoryException;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static challengetask.group02.fsstructure.Entry.TYPE.DIRECTORY;
import static challengetask.group02.fsstructure.Entry.TYPE.FILE;

/*
 * Path resolution routine:
 * the path (relative to the mount point) is given as an input,
 * filesystem Entry is produced as an output.
 */

public class PathResolver {

    public static Entry resolvePath(String path, FSModifyHelper helper) throws IOException, ClassNotFoundException, FsException {
        Path subPaths = Paths.get(path);

        Entry resultEntry;

        //first, get the root directory
        Directory currentDirectory;
        try {
            currentDirectory = (Directory) getEntry(Number160.ZERO, helper);
        } catch (NoSuchFileOrDirectoryException e) {
            throw new FsException("No root node was found. Probably not connected to a P2P network with a running file system.");
        }

        Number160 currentChildFileID;
        Number160 currentChildDirID;

        for (Path dir : subPaths) {
            //try to get the next child ID from the current directory. If the path is correct one will assigned with an ID and one with null.
            currentChildFileID = currentDirectory.getChild(dir.toString(), FILE);
            currentChildDirID = currentDirectory.getChild(dir.toString(), DIRECTORY);

            if (currentChildFileID != null && subPaths.endsWith(dir)) {
                resultEntry = getEntry(currentChildFileID, helper);
                return resultEntry;
            }
            else if (currentChildFileID != null && !subPaths.endsWith(dir)) {
                throw new NotADirectoryException("");
            }
            else if (currentChildDirID == null) {
                throw new NoSuchFileOrDirectoryException(dir.toString());
            } else {
                currentDirectory = (Directory) getEntry(currentChildDirID, helper);
            }
        }
        resultEntry = currentDirectory;

        return resultEntry;
    }

    private static Entry getEntry(Number160 ID, FSModifyHelper helper) throws IOException, ClassNotFoundException, NoSuchFileOrDirectoryException {
        if (ID == null) {
            System.err.println("BUG: trying to get Entry with ID null!");
            throw new NoSuchFileOrDirectoryException("Tried to get entry with ID null.");
        }

        return helper.getEntryByID(ID);
    }
}