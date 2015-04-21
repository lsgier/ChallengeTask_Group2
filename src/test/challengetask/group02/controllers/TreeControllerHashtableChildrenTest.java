package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import net.tomp2p.dht.FutureDHT;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TreeControllerHashtableChildrenTest {
    static final Random RND = new Random( 42L );
    static int nr = 10;
    static int port = 7777;
    static int local = 3;

    static String rootName = "/";


    public static TreeControllerStrategy controller;


    @BeforeClass
    public static void setup() {
        //setup logging to console
        //org.apache.log4j.BasicConfigurator.configure();


        try {
            //initialize network
            PeerDHT[] peers = createAndAttachPeersDHT(nr, port);
            bootstrap(peers);

            //initialize controller with a peer
            controller = new TreeControllerHashtableChildren(peers[local]);


            //create a root node
            Directory rootDir = new Directory(Number160.ZERO, null, rootName);
            //upload root into DHT
            Data data = new Data(rootDir);
            FutureDHT futureDHT = peers[3].put(Number160.ZERO).data(data).start();
            futureDHT.awaitUninterruptibly();

            //create two subdirectory
            Number160 homeDirKey = Number160.createHash("home");
            Number160 binDirKey = Number160.createHash("bin");
            Directory homeDir = new Directory(homeDirKey, Number160.ZERO, "home");
            Directory binDir = new Directory(binDirKey, Number160.ZERO, "bin");

            //upload dirs into DHT
            data = new Data(homeDir);
            FutureDHT futureDHT2 = peers[3].put(homeDirKey).data(data).start();
            data = new Data(binDir);
            futureDHT = peers[4].put(binDirKey).data(data).start();
            //futureDHT2.awaitUninterruptibly();
            //futureDHT.awaitUninterruptibly();

            //link children to parent
            rootDir.addChild("home", homeDirKey, Entry.TYPE.DIRECTORY);
            rootDir.addChild("bin", binDirKey, Entry.TYPE.DIRECTORY);
            data = new Data(rootDir);
            futureDHT = peers[3].put(Number160.ZERO).data(data).start();
            futureDHT.awaitUninterruptibly();


        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFindEntryGetRoot() throws Exception {
        Directory root = (Directory) controller.findEntry("/");
        assertEquals(rootName, root.getEntryName());

        System.out.println("Found a root node named: " + root.getEntryName());


    }

    public void testFindEntry() throws Exception {
        //Number160 key = new Number160(12345);

    }

    public void testGetPath() throws Exception {

    }

    @Test
    public void testCreateDir() throws Exception {
        String testPath = "/newTestDir";
        controller.createDir(testPath);


        System.out.println("testCreateDir-- children of \"/\" after creating /newTestDir \n" + controller.readDir("/") + "\n");
        assertTrue(controller.readDir("/").contains("newTestDir"));

        Directory newDir = (Directory) controller.findEntry(testPath);
        assertEquals(Entry.TYPE.DIRECTORY, newDir.getType());
        assertEquals(testPath, "/" + newDir.getEntryName());

        String testSubPath = testPath+"/subTest";
        controller.createDir(testSubPath);

        System.out.println("testCreateDir-- children of \"/newTestDir/\" after creating /newTestDir/subTest \n" + controller.readDir(testPath) + "\n\n");
        assertTrue(controller.readDir(testPath).contains("subTest"));

        Directory newSubDir = (Directory) controller.findEntry(testSubPath);
        assertEquals(Entry.TYPE.DIRECTORY, newSubDir.getType());
        assertEquals(testSubPath, testPath+"/"+newSubDir.getEntryName());



    }

    @Test
    public void testReadDir() throws Exception {

        ArrayList<String> children = controller.readDir("/");

        assertTrue(children.contains("home"));
        assertTrue(children.contains("bin"));


        for (String child : children) {
            System.out.println(child);
        }

    }


    public static PeerDHT[] createAndAttachPeersDHT( int nr, int port ) throws IOException {
        PeerDHT[] peers = new PeerDHT[nr];
        for ( int i = 0; i < nr; i++ ) {
            if ( i == 0 ) {
                peers[0] = new PeerBuilderDHT(new PeerBuilder( new Number160( RND ) ).ports( port ).start()).start();

            } else {
                peers[i] = new PeerBuilderDHT(new PeerBuilder( new Number160( RND ) ).masterPeer( peers[0].peer() ).start()).start();

            }
        }
        return peers;
    }
    public static void bootstrap( PeerDHT[] peers ) {
        //make perfect bootstrap, the regular can take a while
        for(int i=0;i<peers.length;i++) {
            for(int j=0;j<peers.length;j++) {
                peers[i].peerBean().peerMap().peerFound(peers[j].peerAddress(), null, null, null);

            }

        }
    }
}

