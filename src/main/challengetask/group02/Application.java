package main.challengetask.group02;

import java.io.IOException;
import java.util.Random;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;


public class Application {
	static final Random RND = new Random(42L);
	static final int PEER_NR_1 = 1;
	static final int PEER_NR_2 = 2;	
	static PeerAddress peerAddr;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		PeerDHT master = null;
		final int nrPeers = 1;
		final int port = 4001;
		final int waitingTime = 250;
		try{
			PeerDHT[] peers = createAndAttachPeersDHT(nrPeers, port);
			bootstrap(peers);
			master = peers[0];
			String key  = "Max Powel";
			
			setupReplyHandler(peers);
			examplePutGet(peers, key);
			
			//System.out.println("End.");
			
			Thread.sleep(waitingTime);
		} finally {
			if (master != null) {
				master.shutdown();
			}
		}
	}
	
	private static void examplePutGet(final PeerDHT[] peers, final String key) throws IOException, ClassNotFoundException{
		Number160 nr = new Number160(key.getBytes());
		
		FuturePut futurePut = peers[PEER_NR_1].put(nr).data(new Data(peers[PEER_NR_1].peerAddress())).start();
		futurePut.awaitUninterruptibly();
		System.out.println("Peer "+ PEER_NR_1 +" STORED key: [" + key + "], value: " + peers[PEER_NR_1].peerAddress());
		
		FutureGet futureGet = peers[PEER_NR_2].get(nr).start();
		futureGet.awaitUninterruptibly();
		peerAddr = (PeerAddress) futureGet.data().object();
		System.out.println("Peer "+ PEER_NR_2 + ", with PeerAddress: "+ peers[PEER_NR_2].peerAddress() +" GOT: The Key ["+key+ "] has the value: " + peerAddr);
		
		/*
		Set<Entry<PeerAddress, Map<Number640, Data>>> ids = futureGet.rawData().entrySet();
		System.out.println("IDs of the peers that replied to peer "+ PEER_NR_2 + ":");
		for (Entry<PeerAddress, Map<Number640, Data>> id:ids){
			System.out.println("Peer ID: "+id.getKey().peerId());
		}
		
		
		for (int i=0; i<10; i++){
			System.out.println("Peer array element nr. " + i + " has got the id " + peers[i].peer().peerID());
		}
		*/
		
		// send message
		String message = "hello world";
		FutureDirect futureDirect = peers[PEER_NR_2].peer().sendDirect(peerAddr).object(message).start();
		BaseFutureListener<? extends BaseFuture> listener = new BaseFutureListener<BaseFuture>() {
			public void operationComplete(BaseFuture future) throws Exception {
				System.out.println("Sending message OK, from peer " + PEER_NR_2 + ", with PeerAddress: " + peers[PEER_NR_2].peerAddress());
			}
			public void exceptionCaught(Throwable t) throws Exception {
				System.out.println("Something while sending went wrong!!!");
			}
		};
		futureDirect.addListener(listener);
		
		
	}
	
	// ACK from the receiver
	private static void setupReplyHandler(PeerDHT[] peers) {
		for (final PeerDHT peer : peers) {
			peer.peer().objectDataReply(new ObjectDataReply() {
				public Object reply(PeerAddress sender, Object request) throws Exception {
					System.err.println("I'm " + peer.peerID() + " and I just got the message [" + request
							+ "] from " + sender.peerId());
					return "world";
				}
			});
		}
	}
	

    /**
     * Bootstraps peers to the first peer in the array.
     * 
     * @param peers The peers that should be bootstrapped
     */
    
    
    public static void bootstrap( PeerDHT[] peers ) {
    	//make perfect bootstrap, the regular can take a while
    	for(int i=0;i<peers.length;i++) {
    		for(int j=0;j<peers.length;j++) {
    			peers[i].peerBean().peerMap().peerFound(peers[j].peerAddress(), null, null, null);
    		}
    	}
    }

    /**
     * Create peers with a port and attach it to the first peer in the array.
     * 
     * @param nr The number of peers to be created
     * @param port The port that all the peer listens to. The multiplexing is done via the peer Id
     * @return The created peers
     * @throws IOException IOException
     */
    
    
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
    
    
}