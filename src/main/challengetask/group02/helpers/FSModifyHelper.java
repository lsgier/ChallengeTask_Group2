package challengetask.group02.helpers;

import challengetask.group02.fsstructure.Block;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fsstructure.File;
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
 * This is the class to modify the tree,
 * some methods use simple put, others like addNewEntry and removeEntry
 * utilize vDHT mechanisms.
 *
 * The code for vDHT is mostly copied from ExampleVDHT class from tomp2p examples.
 *
 * Requires further refactoring.
 * */

 public class FSModifyHelper {
    private PeerDHT peer;
    private Random RND = new Random(42L);
    private SimpleCache<Entry> cache = new SimpleCache<>(1);


    public FSModifyHelper(PeerDHT peer) {
        this.peer = peer;
    }

    public int addNewEntry(Directory parentDir, Entry child) {

        try {
            put(child);
            vUpdateParentAddChild(parentDir, child);

        } catch (Exception e) {
        }
        return 0;
    }

    public Entry getEntryByID(Number160 ID) throws IOException, ClassNotFoundException {
        FutureGet futureGet = peer.get(ID).getLatest().start();
        futureGet.awaitUninterruptibly();

        if (futureGet.isEmpty()) {
            System.out.println("getEntryFromID did not get a result -> faulty fs");
        }

        return (Entry) futureGet.data().object();
    }

    public void removeEntry(Directory parent, Entry entry) throws ClassNotFoundException {
        try {
            /*entry.setDirtyBit(true);
            put(entry);*/
            removeEntry(entry);
            vUpdateParentRemoveChild(parent, entry);

        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeEntryFromParent(Directory parent, Entry entry) throws ClassNotFoundException {
        try {
            /*entry.setDirtyBit(true);
            put(entry);*/

            vUpdateParentRemoveChild(parent, entry);

        } catch (IOException e) {

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

            parent.renameChild(entry.getEntryName(), newName);
            xPut(parent);

            entry.setEntryName(newName);
            put(entry);

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

            FuturePut futurePut = peer.put(block.getID()).data(new Data(block)).start();

            futurePut.awaitUninterruptibly();

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
        try {
            removeEntryFromParent(oldParent, entry);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        entry.setEntryName(newName);
        addNewEntry(newParent, entry);

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