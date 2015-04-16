package playground;

import challengetask.group02.controllers.ControllerContext;
import challengetask.group02.fuserunner.FuseRunner;
import net.fusejna.FuseException;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;

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
            } catch (IOException e) {
                e.printStackTrace();
            }
            bootstrap(peers);


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

        }
    }

}
