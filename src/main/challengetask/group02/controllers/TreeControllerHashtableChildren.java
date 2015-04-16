package challengetask.group02.controllers;

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
import java.util.ArrayList;
import java.util.Hashtable;

import static challengetask.group02.fsstructure.Entry.TYPE.DIRECTORY;
import static challengetask.group02.fsstructure.Entry.TYPE.FILE;

public class TreeControllerHashtableChildren implements TreeControllerStrategy {

    PeerDHT peer;

    public TreeControllerHashtableChildren(PeerDHT peer) {
        this.peer = peer;
    }

    private String currentDirectory;
    private Number160 currentDirectoryID;

    private Entry getEntryFromID(Number160 ID) throws IOException, ClassNotFoundException {
        /*TODO things that can go wrong here
        * (basic DHT stuff; put and get)
        * fs errors:
        * - no result->why was this referenced
        * - child name not found
        *
        *
        * */

        FutureGet futureGet = peer.get(ID).start();
        futureGet.awaitUninterruptibly();
        return (Entry) futureGet.data().object();
    }

    public Entry findEntry(String path) throws IOException, ClassNotFoundException {
        Path subPaths = Paths.get(path);

        //TODO IDEA could another controller implementation use a cache or something? it could remember the last used path.
        //->typically multiple operations happen in the same directory.

        //first, get the root directory
        //TODO QUESTION create root if root is not found?
        Directory currentDir = (Directory) getEntryFromID(Number160.ZERO);

        for(Path dir: subPaths) {
            currentDir = (Directory) getEntryFromID(currentDir.getChild(dir.toString(), DIRECTORY));
         }

        return currentDir;
    }

    public String getPath(Number160 EntryID) {
        return null;
    }

    public void createDir(String path) {

    }

    public ArrayList<String> readDir(String path) throws IOException, ClassNotFoundException {
        Directory dir = (Directory) findEntry(path);
        Hashtable<String, Number160> children = dir.getChildren(FILE);
        children.putAll(dir.getChildren(DIRECTORY));

        return new ArrayList<String>(children.keySet());
    }





}
