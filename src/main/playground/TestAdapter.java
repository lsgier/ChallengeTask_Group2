package playground;

import challengetask.group02.controllers.ControllerContext;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fuserunner.FuseRunner;
import net.fusejna.FuseException;
import net.tomp2p.dht.FutureDHT;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.util.Random;

public class TestAdapter {

    static final Random RND = new Random( 42L );

    static ControllerContext treeController;

    public static ControllerContext getController() {
        if (treeController == null) {
            //initialize network
            PeerDHT[] peers = new PeerDHT[0];
            try {
                peers = createAndAttachPeersDHT(10, 7777);

                bootstrap(peers);

                //create a root node
                Directory rootDir = new Directory(Number160.ZERO, null, "rootNodeName");
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



            } catch (IOException e) {
                e.printStackTrace();
            }



            treeController = new ControllerContext(peers[3]);
        }


        return treeController;
    }

    public static void main(String args[]) throws FuseException {
        FuseRunner rrrrrrrrr = new FuseRunner(getController(), "./TestMount");
        rrrrrrrrr.run();
    }


    public static PeerDHT[] createAndAttachPeersDHT(int nr, int port) throws IOException {
        PeerDHT[] peers = new PeerDHT[nr];
        for (int i = 0; i < nr; i++) {
            if (i == 0) {
                peers[0] = new PeerBuilderDHT(new PeerBuilder(new Number160(RND)).ports(port).start()).start();

            } else {
                peers[i] = new PeerBuilderDHT(new PeerBuilder(new Number160(RND)).masterPeer(peers[0].peer()).start()).start();

            }
        }
        return peers;
    }

    public static void bootstrap(PeerDHT[] peers) {
        //make perfect bootstrap, the regular can take a while
        for (int i = 0; i < peers.length; i++) {
            for (int j = 0; j < peers.length; j++) {
                peers[i].peerBean().peerMap().peerFound(peers[j].peerAddress(), null, null, null);

            }
            System.out.println("Bootstrapped peer " + i);

        }
    }

}
