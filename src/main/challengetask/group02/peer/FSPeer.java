package challengetask.group02.peer;

import challengetask.group02.controllers.ControllerContext;
import challengetask.group02.fsstructure.Block;
import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.File;
import challengetask.group02.fuseimpl.Fuse;
import net.tomp2p.connection.*;
import net.tomp2p.dht.*;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import net.tomp2p.replication.IndirectReplication;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
Main class
responsible for starting peers as a server or as a "slave" peer which will bootstrap to master
and after runs the fuse-related routine.

Arguments:
1. mount point, obligatory, a path to an empty directory
2. bootstrap-ip, optional, required for bootsrapping
3. peer-number, optional, needed for local testing
 */

public class FSPeer {
    private static PeerDHT peerDHT;
    private static Logger logger = null;
    private static FileHandler fh;

    public static void main(String[] args) throws Exception {
        String mountPoint = args[0];

        if (args.length == 1){
            //if we run just server, only create peer.log
            initLogger("master.");
            startServer();
            createRoot();


        }

        if (args.length > 1){
            String serverIP = args[1];
            int port;

            if (args.length == 3){
                port = Integer.parseInt(args[2]);
                initLogger(Integer.toString(port)+".");
            } else {
                initLogger("");
                port = 4000;
            }
            startClient(serverIP, port);
        }







        new Fuse(new ControllerContext(peerDHT), mountPoint).run(logger);
    }

    private static void initLogger(String prefix) {
        logger = Logger.getLogger("mainLog");
        try {
            fh = new FileHandler(prefix+"peer.log", true);
            logger.addHandler(fh);
            logger.setLevel(Level.INFO);

            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startServer() throws Exception {

        Random rnd = new Random(43L);
        Bindings b = new Bindings().addProtocol(StandardProtocolFamily.INET).addAddress(InetAddress.getByName("127.0.0.1"));
        peerDHT = new PeerBuilderDHT(new PeerBuilder(new Number160(rnd)).ports(4000).start()).start();

       // new IndirectReplication(peerDHT).replicationFactor(3).start();

        System.out.println("Server started Listening to: " + DiscoverNetworks.discoverInterfaces(b));
        System.out.println("address visible to outside is " + peerDHT.peerAddress());

    }
    /*
    Every time server starts it creates the root directory with a key ZERO,
    this method guarantees that root always exists in the file system.
     */
    private static void createRoot() throws IOException {
        //create a root node
        Directory rootDir = new Directory(Number160.ZERO, "root");
        //upload root into DHT
        Data data = new Data(rootDir);
        FutureDHT futureDHT = peerDHT.put(Number160.ZERO).data(data).start();
        futureDHT.awaitUninterruptibly();
    }

    public static void startClient(String ipAddress, int port) throws Exception {
        Random rnd = new Random();
        Bindings b = new Bindings().addProtocol(StandardProtocolFamily.INET).addAddress(InetAddress.getByName("127.0.0.1"));

        peerDHT = new PeerBuilderDHT(new PeerBuilder(new Number160(rnd)).ports(port).start()).start();
       // new IndirectReplication(peerDHT).replicationFactor(3).start();
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
            //throw new P2PException("can not connect");
        }
    }
}