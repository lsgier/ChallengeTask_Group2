package main.challengetask.group02;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import net.tomp2p.connection.Bindings;
import net.tomp2p.connection.ChannelCreator;
import net.tomp2p.connection.DefaultConnectionConfiguration;
import net.tomp2p.connection.DiscoverNetworks;
import net.tomp2p.connection.StandardProtocolFamily;
import net.tomp2p.futures.FutureChannelCreator;
import net.tomp2p.futures.FutureResponse;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

public class InitialPeer {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Random rnd = new Random(43L);
		Bindings b = new Bindings().addProtocol(StandardProtocolFamily.INET).addAddress(
		        InetAddress.getByName("127.0.0.1"));
		// b.addInterface("eth0");
		Peer master = new PeerBuilder(new Number160(rnd)).ports(4000).bindings(b).start();
		System.out.println("Server started Listening to: " + DiscoverNetworks.discoverInterfaces(b));
		System.out.println("address visible to outside is " + master.peerAddress());
		while (true) {
			for (PeerAddress pa : master.peerBean().peerMap().all()) {
				System.out.println("PeerAddress: " + pa);
				FutureChannelCreator fcc = master.connectionBean().reservation().create(1, 1);
				fcc.awaitUninterruptibly();

				ChannelCreator cc = fcc.channelCreator();

				FutureResponse fr1 = master.pingRPC().pingTCP(pa, cc, new DefaultConnectionConfiguration());
				fr1.awaitUninterruptibly();

				if (fr1.isSuccess()) {
					System.out.println("peer online T:" + pa);
				} else {
					System.out.println("offline " + pa);
				}

				FutureResponse fr2 = master.pingRPC().pingUDP(pa, cc, new DefaultConnectionConfiguration());
				fr2.awaitUninterruptibly();

				cc.shutdown();

				if (fr2.isSuccess()) {
					System.out.println("peer online U:" + pa);
				} else {
					System.out.println("offline " + pa);
				}
			}
			Thread.sleep(1500);
		}
	}
}