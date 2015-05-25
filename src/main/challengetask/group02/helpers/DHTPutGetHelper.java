package challengetask.group02.helpers;

import challengetask.group02.fsstructure.Block;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fsstructure.File;
import net.tomp2p.dht.FutureDHT;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.FutureRemove;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;
import net.tomp2p.utils.Pair;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * Created by anvar on 25/04/15.
 */

/**
 * This is the end-point class to work with the tree
 * just right now it does things the same way as before (no checks, direct put)
 * further will be expanded with vDHT mechanisms
 * <p>
 * The general guideline is: if there's a need to use peer.put() at some point,
 * utilize this class.
 * <p>
 * As input the class accepts entries plus the things that have to be changed, but not
 * the entries that already updated. That will allow to use vDHT and frees us
 * from writing the Entry -> Action mechanism classes.
 * <p>
 * The "get" part of the Helper will probably be also written -- every time we
 * get something from DHT, consistency also have to be checked. For that need
 * code, but later. Maybe instead will have ConsistentPutHelper and ConsistentGetHelper
 * or something like it.
 */
public class DHTPutGetHelper {
    PeerDHT peer;
    private Random RND = new Random(42L);

    public DHTPutGetHelper(PeerDHT peer) {
        this.peer = peer;
    }

    public int addNewEntry(Directory parentDir, Entry child) {
//first have to update parent
        try {
            put(child);
            vUpdateParentAddChild(parentDir, child);
//here have to check if everything went fine,
//otherwise have to remove child
        } catch (Exception e) {
        }
        return 0;
    }

    public void removeEntry(Directory parent, Entry entry) throws ClassNotFoundException {
        try {
            entry.setDirtyBit(true);
            put(entry);
            removeEntry(entry);
            vUpdateParentRemoveChild(parent, entry);
//parent.removeChild(entry.getEntryName());
//put(parent);
        } catch (IOException e) {
//TODO
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void put(Entry entry) throws IOException {
        FuturePut futurePut = peer.put(entry.getID()).data(new Data(entry)).start();
        futurePut.awaitUninterruptibly();
    }

    private void put(Block block) throws IOException {
        FuturePut futurePut = peer.put(block.getID()).data(new Data(block)).start();
        futurePut.awaitUninterruptibly();
    }

    public int updateEntryName(Directory parent, Entry entry, String newName) {
        try {
//Modify parent
            parent.renameChild(entry.getEntryName(), newName);
            xPut(parent);
//if previous was successfull, can put the child itself
            entry.setEntryName(newName);
            put(entry);
//if this was successful either, then all is ok.
        } catch (Exception e) {
        }
        return 0;
    }

    private void xPut(Entry entry) throws IOException {
        FutureGet fg = peer.get(entry.getID()).getLatest().start()
                .awaitUninterruptibly();
        Pair<Number640, Data> pair = checkVersions(fg.rawData());
        Data newData = new Data(entry);
        Number160 v = pair.element0().versionKey();
        long version = v.timestamp() + 1;
        newData.addBasedOn(v);
        Pair<Number160, Data> pair3 = new Pair<Number160, Data>(new Number160(version,
                newData.hash()), newData);
        FuturePut fp1 = peer.put(entry.getID()).data(Number160.ZERO, pair3.element1().prepareFlag(), pair3.element0()).start().awaitUninterruptibly();
        Pair<Number640, Byte> pair2 = checkVersions(fp1.rawResult());
        FuturePut fp = peer.put(entry.getID())
                .versionKey(pair2.element0().versionKey()).putConfirm()
                .data(new Data()).start().awaitUninterruptibly();
    }

    public void putFile(Number160 ID, File file) {
        try {
            put(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putBlock(Number160 ID, Block block) {
        try {
            put(block);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Block getBlockDHT(Number160 ID) {
        Block block;
        try {
            FutureGet futureGet = peer.get(ID).start();
            futureGet.awaitUninterruptibly();
            block = (Block) futureGet.data().object();
            return block;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int moveEntry(Directory newParent, Directory oldParent, Entry entry, String newName) {
        addNewEntry(newParent, entry);
        try {
            removeEntry(oldParent, entry);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void removeEntry(Entry entry) {
        //TODO asynchronous
        FutureRemove future = peer.remove(entry.getID()).all().start();
        future.awaitUninterruptibly();
    }

    /**
     * This method deletes (removes from DHT) all the blocks of the file, so that the file object can be safely deleted.
     *
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

    public void flushFile(File file) {
        file.setReadOnly(false);
        file.setModifierPeer(null);
        try {
            put(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void vUpdateParentAddChild(Directory parentDir, Entry child)
            throws ClassNotFoundException, InterruptedException, IOException {
        Pair<Number640, Byte> pair2 = null;
        for (int i = 0; i < 5; i++) {
            Pair<Number160, Data> pair = getAndUpdate(parentDir, child);
            if (pair == null) {
                System.out
                        .println("we cannot handle this kind of inconsistency automatically, handing over the the API dev");
                return;
            }
            FuturePut fp = peer
                    .put(parentDir.getID())
                    .data(Number160.ZERO, pair.element1().prepareFlag(), pair.element0()).start().awaitUninterruptibly();
            pair2 = checkVersions(fp.rawResult());
            // 1 is PutStatus.OK_PREPARED
            if (pair2 != null && pair2.element1() == 1) {
                break;
            }
            System.out.println("get delay or fork - put");
            // if not removed, a low ttl will eventually get rid of it
            peer.remove(parentDir.getID()).versionKey(pair.element0()).start()
                    .awaitUninterruptibly();
            Thread.sleep(RND.nextInt(500));
        }
        if (pair2 != null && pair2.element1() == 1) {
        //stored
            FuturePut fp = peer.put(parentDir.getID())
                    .versionKey(pair2.element0().versionKey()).putConfirm()
                    .data(new Data()).start().awaitUninterruptibly();
        } else {
            System.out
                    .println("we cannot handle this kind of inconsistency automatically, handing over the the API dev");
        }
    }

    private Pair<Number160, Data> getAndUpdate(Directory parentDir, Entry child) throws InterruptedException, ClassNotFoundException,
            IOException {
        Random RND = new Random(42L);
        Pair<Number640, Data> pair = tryToGet(parentDir.getID());
        // we got the latest data
        if (pair != null) {
            // update operation is append
            parentDir.addChild(child.getEntryName(), child.getID(), child.getType());
            Data newData = new Data(parentDir);
            Number160 v = pair.element0().versionKey();
            long version = v.timestamp() + 1;
            newData.addBasedOn(v);
            //since we create a new version, we can access old versions as well
            return new Pair<Number160, Data>(new Number160(version,
                    newData.hash()), newData);
        }
        return null;
    }

    private void vUpdateParentRemoveChild(Directory parentDir, Entry child) throws InterruptedException, IOException, ClassNotFoundException {
        Pair<Number640, Byte> pair2 = null;
        for (int i = 0; i < 5; i++) {
            Pair<Number160, Data> pair = getAndUpdate_remove(parentDir, child);
            if (pair == null) {
                System.out
                        .println("we cannot handle this kind of inconsistency automatically, handing over the the API dev");
                return;
            }
            FuturePut fp = peer
                    .put(parentDir.getID())
                    .data(Number160.ZERO, pair.element1().prepareFlag(), pair.element0()).start().awaitUninterruptibly();
            pair2 = checkVersions(fp.rawResult());
            // 1 is PutStatus.OK_PREPARED
            if (pair2 != null && pair2.element1() == 1) {
                break;
            }
            System.out.println("get delay or fork - put");
            // if not removed, a low ttl will eventually get rid of it
            peer.remove(parentDir.getID()).versionKey(pair.element0()).start()
                    .awaitUninterruptibly();
            try {
                Thread.sleep(RND.nextInt(500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (pair2 != null && pair2.element1() == 1) {
        //stored
            FuturePut fp = peer.put(parentDir.getID())
                    .versionKey(pair2.element0().versionKey()).putConfirm()
                    .data(new Data()).start().awaitUninterruptibly();
        } else {
            System.out
                    .println("we cannot handle this kind of inconsistency automatically, handing over the the API dev");
        }
    }

    private Pair<Number160, Data> getAndUpdate_remove(Directory parentDir, Entry child) throws InterruptedException, ClassNotFoundException,
            IOException {
        Random RND = new Random(42L);
        Pair<Number640, Data> pair = tryToGet(parentDir.getID());
        // we got the latest data
        if (pair != null) {
        // update operation is append
            parentDir.removeChild(child.getEntryName());
            Data newData = new Data(parentDir);
            Number160 v = pair.element0().versionKey();
            long version = v.timestamp() + 1;
            newData.addBasedOn(v);
        //since we create a new version, we can access old versions as well
            return new Pair<Number160, Data>(new Number160(version,
                    newData.hash()), newData);
        }
        return null;
    }

    private Pair<Number640, Data> tryToGet(Number160 key) {
        Pair<Number640, Data> pair = null;
        for (int i = 0; i < 5; i++) {
            FutureGet fg = peer.get(key).getLatest().start()
                    .awaitUninterruptibly();
            // check if all the peers agree on the same latest version, if not
            // wait a little and try again
            pair = checkVersions(fg.rawData());
            if (pair != null) {
                break;
            }
            // something went wrong, have to wait
            try {
                Thread.sleep(RND.nextInt(500));
            } catch (InterruptedException e) {
                //TODO find out what is it
                e.printStackTrace();
            }
        }
        return pair;
    }

    private static <K> Pair<Number640, K> checkVersions(
            Map<PeerAddress, Map<Number640, K>> rawData) {
        Number640 latestKey = null;
        K latestData = null;
        for (Map.Entry<PeerAddress, Map<Number640, K>> entry : rawData
                .entrySet()) {
            if (latestData == null && latestKey == null) {
                latestData = entry.getValue().values().iterator().next();
                latestKey = entry.getValue().keySet().iterator().next();
            } else {
                if (!latestKey.equals(entry.getValue().keySet().iterator()
                        .next())
                        || !latestData.equals(entry.getValue().values()
                        .iterator().next())) {
                    return null;
                }
            }
        }
        return new Pair<Number640, K>(latestKey, latestData);
    }
}