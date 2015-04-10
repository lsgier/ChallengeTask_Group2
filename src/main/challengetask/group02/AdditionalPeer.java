package challengetask.group02;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import net.tomp2p.connection.Bindings;
import net.tomp2p.connection.DiscoverNetworks;
import net.tomp2p.connection.StandardProtocolFamily;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

public class AdditionalPeer {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Random rnd = new Random();
		Bindings b = new Bindings().addProtocol(StandardProtocolFamily.INET).addAddress(
		        InetAddress.getByName("127.0.0.1"));
		// b.addInterface("eth0");
		Peer client = new PeerBuilder(new Number160(rnd)).ports(4001).bindings(b).start();
		System.out.println("AdditionalPeer started and Listening to: " + DiscoverNetworks.discoverInterfaces(b));
		System.out.println("address visible to outside is " + client.peerAddress());

		String ipAddress = (args[0]);
		InetAddress address = Inet4Address.getByName(ipAddress);
		int masterPort = 4000;
		PeerAddress pa = new PeerAddress(Number160.ZERO, address, masterPort, masterPort);

		System.out.println("PeerAddress: " + pa);
		
		// Future Discover
		FutureDiscover futureDiscover = client.discover().inetAddress(address).ports(masterPort).start();
		futureDiscover.awaitUninterruptibly();

		// Future Bootstrap - slave
		FutureBootstrap futureBootstrap = client.bootstrap().inetAddress(address).ports(masterPort).start();
		futureBootstrap.awaitUninterruptibly();

		Collection<PeerAddress> addressList = client.peerBean().peerMap().all();
		System.out.println(addressList.size());

		if (futureDiscover.isSuccess()) {
			System.out.println("found that my outside address is " + futureDiscover.peerAddress());
		} else {
			System.err.println("failed " + futureDiscover.failedReason());
		}
		client.shutdown();
	}

}
