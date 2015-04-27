package prototype;

import challengetask.group02.controllers.ControllerContext;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fuserunner.FuseRunner;
import net.tomp2p.connection.*;
import net.tomp2p.dht.*;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

/**
 * Created by riccardo on 23.04.15.
 */
public class FSPeer {
    private static PeerDHT peerDHT;

    public static void main(String[] args) throws Exception {
        String mountPoint = args[0];

        if (args.length > 1) {
            String serverIP = args[1];
            startClient(serverIP);
        } else {
            startServer();
            createRoot();
        }
        new FuseRunner(new ControllerContext(peerDHT), mountPoint).run();
    }

    public static void startServer() throws Exception {

        Random rnd = new Random(43L);

        Bindings b = new Bindings().addProtocol(StandardProtocolFamily.INET).addAddress(InetAddress.getByName("127.0.0.1"));

        peerDHT = new PeerBuilderDHT(new PeerBuilder(new Number160(rnd)).ports(4000).start()).start();

        System.out.println("Server started Listening to: " + DiscoverNetworks.discoverInterfaces(b));
        System.out.println("address visible to outside is " + peerDHT.peerAddress());


    }
    private static void createRoot() throws IOException {
        //create a root node
        Directory rootDir = new Directory(Number160.ZERO, null, "rootNodeName");
        //upload root into DHT
        Data data = new Data(rootDir);
        FutureDHT futureDHT = peerDHT.put(Number160.ZERO).data(data).start();
        futureDHT.awaitUninterruptibly();
    }

    public static void startClient(String ipAddress) throws Exception {
        Random rnd = new Random();
        Bindings b = new Bindings().addProtocol(StandardProtocolFamily.INET).addAddress(InetAddress.getByName("127.0.0.1"));

        peerDHT = new PeerBuilderDHT(new PeerBuilder(new Number160(rnd)).ports(4001).start()).start();
        System.out.println("Client started and Listening to: " + DiscoverNetworks.discoverInterfaces(b));
        System.out.println("address visible to outside is " + peerDHT.peerAddress());

        InetAddress address = Inet4Address.getByName(ipAddress);
        int masterPort = 4000;
        PeerAddress pa = new PeerAddress(Number160.ZERO, address, masterPort, masterPort);

        System.out.println("PeerAddress: " + pa);

        // Future Discover
        FutureDiscover futureDiscover = peerDHT.peer().discover().inetAddress(address).ports(masterPort).start();
        futureDiscover.awaitUninterruptibly();

        // Future Bootstrap - slave
        FutureBootstrap futureBootstrap = peerDHT.peer().bootstrap().inetAddress(address).ports(masterPort).start();
        futureBootstrap.awaitUninterruptibly();

        Collection<PeerAddress> addressList = peerDHT.peerBean().peerMap().all();
        System.out.println(addressList.size());

        if (futureDiscover.isSuccess()) {
            System.out.println("found that my outside address is " + futureDiscover.peerAddress());
        } else {
            System.err.println("failed " + futureDiscover.failedReason());
        }

    }

}