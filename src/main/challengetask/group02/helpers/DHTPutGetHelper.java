package challengetask.group02.helpers;

import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;

import challengetask.group02.fsstructure.File;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.FutureRemove;
import net.tomp2p.dht.PeerDHT;

import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.util.Map;

/**
 * Created by anvar on 25/04/15.
 */

/**
 *
 * This is the end-point class to work with the tree
 * just right now it does things the same way as before (no checks, direct put)
 * further will be expanded with vDHT mechanisms
 *
 * The general guideline is: if there's a need to use peer.put() at some point,
 * utilize this class.

 * As input the class accepts entries plus the things that have to be changed, but not
 * the entries that already updated. That will allow to use vDHT and frees us
 * from writing the Entry -> Action mechanism classes.
 *
 * The "get" part of the Helper will probably be also written -- every time we
 * get something from DHT, consistency also have to be checked. For that need
 * code, but later. Maybe instead will have ConsistentPutHelper and ConsistentGetHelper
 * or something like it.
 *
 */
public class DHTPutGetHelper {
    PeerDHT peer;

    public DHTPutGetHelper(PeerDHT peer) {
        this.peer = peer;
    }

    private void put(Entry entry) throws IOException {
        FuturePut futurePut = peer.put(entry.getID()).data(new Data(entry)).start();
        futurePut.awaitUninterruptibly();
    }

    public int addNewEntry(Directory parentDir, Entry child){
        //first have to update parent
        try {
            parentDir.addChild(child.getEntryName(), child.getID(), child.getType());
            put(parentDir);
            //if previous was successfull, can put the child itself
            put(child);
            //if this was successful either, then all is ok.
        } catch (Exception e){

        }
        return 0;
    }

    public int updateEntryName(Directory parent, Entry entry, String newName) {

        try{
            //Modify parent
            parent.renameChild(entry.getEntryName(), newName);
            put(parent);
            //if previous was successfull, can put the child itself
            entry.setEntryName(newName);
            put(entry);
            //if this was successful either, then all is ok.
        } catch (Exception e){

        }

        return 0;
    }

    //TODO QUESTION why does this return an integer?
    public int moveEntry(Directory newParent, Directory oldParent, Entry entry, String newName) {
        newParent.addChild(newName, entry.getID(), entry.getType());
        oldParent.removeChild(entry.getEntryName());
        entry.setEntryName(newName);
        entry.setParentID(newParent.getID());

        try{
            //trying to update new parent first
            put(newParent);
            //if ok, have to modify the old parent
            put(oldParent);
            //if ok, can finally update the entry itself
            put(entry);
        } catch (Exception e){
            //TODO
        }
        return 0;
    }

    public void removeAndDeleteChild(Directory parent, Entry entry) {
        try {
            entry.setDirtyBit(true);
            put(entry);

            parent.removeChild(entry.getEntryName());
            put(parent);

            removeEntry(entry);
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }

    }

    public void removeEntry(Entry entry) {
        //TODO asynchronous
        FutureRemove future = peer.remove(entry.getID()).start();
        future.awaitUninterruptibly();
    }

    /**
     * This method deletes (removes from DHT) all the blocks of the file, so that the file object can be safely deleted.
     * @param file The file to be cleared.
     */
    public void clearAndDeleteFile(File file) {
        file.setDirtyBit(true);
        try {
            put(file);

            for (Number160 number160 : file.getBlocks()) {
                //TODO asynchronous: callbacks are counted, after all deletions called back, resume with deleting the file
                FutureRemove future = peer.remove(number160).start();
                future.awaitUninterruptibly();
            }




        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
