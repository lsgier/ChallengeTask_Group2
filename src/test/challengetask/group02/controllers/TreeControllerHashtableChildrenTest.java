package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Directory;
import junit.framework.TestCase;
import net.tomp2p.dht.FutureDHT;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static challengetask.group02.fsstructure.Entry.TYPE.DIRECTORY;

public class TreeControllerHashtableChildrenTest {
    static final Random RND = new Random( 42L );
    static int nr = 10;
    static int port = 7777;
    static int local = 3;


    public static TreeControllerStrategy controller;


    @BeforeClass
    public static void setup() {
        try {
            //initialize network
            PeerDHT[] peers = createAndAttachPeersDHT(nr, port);
            bootstrap(peers);

            //initialize controller with a peer
            controller = new TreeControllerHashtableChildren(peers[local]);


            //create a root node
            Directory root = new Directory(Number160.ZERO, null, "rootName");
            //upload root into DHT
            Data data = new Data(root);
            FutureDHT futureDHT = peers[3].put(Number160.ZERO).data(data).start();
            futureDHT.awaitUninterruptibly();



        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFindEntryGetRoot() throws Exception {
        Directory root = (Directory) controller.findEntry("/");
        System.out.println("Found a root node named: " + root.getEntryName());


    }

    public void testFindEntry() throws Exception {
        //Number160 key = new Number160(12345);

    }

    public void testGetPath() throws Exception {

    }

    public void testCreateDir() throws Exception {

    }

    public void testReadDir() throws Exception {

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