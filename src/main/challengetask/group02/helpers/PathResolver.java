package challengetask.group02.helpers;


import challengetask.group02.controllers.exceptions.FsException;
import challengetask.group02.controllers.exceptions.NoSuchFileOrDirectoryException;
import challengetask.group02.controllers.exceptions.NotADirectoryException;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static challengetask.group02.fsstructure.Entry.TYPE.DIRECTORY;
import static challengetask.group02.fsstructure.Entry.TYPE.FILE;

/*
 * Path resolution routine:
 * the path (relative to the mount point) is given as an input,
 * filesystem Entry is produced as an output.
 */

public class PathResolver {

    PeerDHT peer;

    SimpleCache<Entry> cache = new SimpleCache<>(1);

    public PathResolver(PeerDHT peer) {
        this.peer = peer;
    }

    public Entry resolvePath(String path) throws IOException, ClassNotFoundException, FsException {
        Path subPaths = Paths.get(path);

        Entry resultEntry = cache.get(path);
        if (resultEntry != null) {
            return resultEntry;
        }

        //first, get the root directory
        Directory currentDirectory;
        try {
            currentDirectory = (Directory) getEntry(Number160.ZERO);
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
                resultEntry = getEntry(currentChildFileID);
                cache.put(path, resultEntry);
                return resultEntry;
            }
            else if (currentChildFileID != null && !subPaths.endsWith(dir)) {
                throw new NotADirectoryException("");
            }
            else if (currentChildDirID == null) {
                throw new NoSuchFileOrDirectoryException(dir.toString());
            } else {
                currentDirectory = (Directory) getEntry(currentChildDirID);
            }
        }
        resultEntry = currentDirectory;
        cache.put(path, resultEntry);

        NavigableMap<Number640, Data> storage = (peer.storageLayer().get());

        Set<Map.Entry<Number640, Data>> entrySet = storage.entrySet();

        for(Map.Entry<Number640, Data> entry: entrySet){

            Number640 x = entry.getKey();
            Number160 version = x.versionKey();



            if (entry.getValue().object().getClass().getName() != "challengetask.group02.fsstructure.Block"){
                Entry e = (Entry)entry.getValue().object();

                System.out.println("id: " + e.getID() + " version: " + version + " name: " + e.getEntryName());

                if (e.getType() == Entry.TYPE.DIRECTORY){
                    System.out.println("dir detected, listing children: ");
                    Directory dir = (Directory) e;
                    Hashtable<String, Number160> children = dir.getChildren();

                    Iterator<Map.Entry<String, Number160>> it = children.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry<String, Number160> nxt = it.next();

                        // Remove entry if key is null or equals 0.
                        System.out.println(nxt.getValue());
                    }

                }
            }


        }


        return resultEntry;
    }

    private Entry getEntry(Number160 ID) throws IOException, ClassNotFoundException, NoSuchFileOrDirectoryException {
        if (ID == null) {
            System.err.println("BUG: trying to get Entry with ID null!");
            throw new NoSuchFileOrDirectoryException("Tried to get entry with ID null.");
        }

        FutureGet futureGet = peer.get(ID).getLatest().start();
        futureGet.awaitUninterruptibly();

        if (futureGet.isEmpty()) {
            System.out.println("getEntryFromID did not get a result -> faulty fs");
            throw new NoSuchFileOrDirectoryException("");
        }

        return (Entry) futureGet.data().object();
    }
}