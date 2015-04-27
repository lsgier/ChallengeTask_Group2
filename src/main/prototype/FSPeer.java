package prototype;

import challengetask.group02.Utils;
import challengetask.group02.controllers.ControllerContext;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.fuserunner.FuseRunner;
import net.tomp2p.connection.*;
import net.tomp2p.dht.*;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureChannelCreator;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.futures.FutureResponse;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;
import net.tomp2p.utils.Pair;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by riccardo on 23.04.15.
 */
public class FSPeer {

    static ControllerContext treeController;

    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            startClient(args[0], args[1]);
        } else {
            startServer(args[0]);
        }

        FuseRunner rrrrrrrrr = new FuseRunner(treeController, args[0]);
        rrrrrrrrr.run();
    }

    //private static final Random RND = new Random(42L);

    public static void startServer( String mountPoint) throws Exception {

        Random rnd = new Random(43L);

        Bindings b = new Bindings().addProtocol(StandardProtocolFamily.INET).addAddress(InetAddress.getByName("127.0.0.1"));
        // b.addInterface("eth0");
        //PeerDHT firstPeer = new PeerBuilder(new Number160(RND)).ports(4000).bindings(b).start();
        PeerDHT firstPeer = new PeerBuilderDHT(new PeerBuilder(new Number160(rnd)).ports(4000).start()).start();

        System.out.println("Server started Listening to: " + DiscoverNetworks.discoverInterfaces(b));
        System.out.println("address visible to outside is " + firstPeer.peerAddress());


        //create a root node
        Directory rootDir = new Directory(Number160.ZERO, null, "rootNodeName");
        //upload root into DHT
        Data data = new Data(rootDir);
        FutureDHT futureDHT = firstPeer.put(Number160.ZERO).data(data).start();
        futureDHT.awaitUninterruptibly();

        treeController = new ControllerContext(firstPeer);



        /*
        while (true) {
            for (PeerAddress pa : firstPeer.peerBean().peerMap().all()) {
                System.out.println("PeerAddress: " + pa);
                FutureChannelCreator fcc = firstPeer.peer().connectionBean().reservation().create(1, 1);
                fcc.awaitUninterruptibly();

                ChannelCreator cc = fcc.channelCreator();

                FutureResponse fr1 = firstPeer.peer().pingRPC().pingTCP(pa, cc, new DefaultConnectionConfiguration());
                fr1.awaitUninterruptibly();

                if (fr1.isSuccess()) {
                    System.out.println("peer online TCP:" + pa);
                } else {
                    System.out.println("offline " + pa);
                }

                FutureResponse fr2 = firstPeer.peer().pingRPC().pingUDP(pa, cc, new DefaultConnectionConfiguration());
                fr2.awaitUninterruptibly();

                cc.shutdown();

                if (fr2.isSuccess()) {
                    System.out.println("peer online UDP:" + pa);
                } else {
                    System.out.println("offline " + pa);
                }
            }
            Thread.sleep(1500);
        }
        */
    }

    public static void startClient(String root, String ipAddress) throws Exception {
        Random rnd = new Random();
        Bindings b = new Bindings().addProtocol(StandardProtocolFamily.INET).addAddress(InetAddress.getByName("127.0.0.1"));
        // b.addInterface("eth0");
        PeerDHT peer = new PeerBuilderDHT(new PeerBuilder(new Number160(rnd)).ports(4001).start()).start();;
        System.out.println("Client started and Listening to: " + DiscoverNetworks.discoverInterfaces(b));
        System.out.println("address visible to outside is " + peer.peerAddress());

        InetAddress address = Inet4Address.getByName(ipAddress);
        int masterPort = 4000;
        PeerAddress pa = new PeerAddress(Number160.ZERO, address, masterPort, masterPort);

        System.out.println("PeerAddress: " + pa);

        // Future Discover
        FutureDiscover futureDiscover = peer.peer().discover().inetAddress(address).ports(masterPort).start();
        futureDiscover.awaitUninterruptibly();

        // Future Bootstrap - slave
        FutureBootstrap futureBootstrap = peer.peer().bootstrap().inetAddress(address).ports(masterPort).start();
        futureBootstrap.awaitUninterruptibly();

        Collection<PeerAddress> addressList = peer.peerBean().peerMap().all();
        System.out.println(addressList.size());

        if (futureDiscover.isSuccess()) {
            System.out.println("found that my outside address is " + futureDiscover.peerAddress());
        } else {
            System.err.println("failed " + futureDiscover.failedReason());
        }
        //peer.shutdown();
        treeController = new ControllerContext(peer);
    }










}