package challengetask.group02.controllers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Random;
import java.util.zip.CRC32;

import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import challengetask.group02.Constants;
import challengetask.group02.fsstructure.File;
import challengetask.group02.controllers.FileContentController;
import challengetask.group02.fsstructure.Block;

public class FileContentControllerTest {
	
	private static String str = ("asdfaölskjfölkewj"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf");
	
	private static PeerDHT[] peer;
	private static FileContentController fcc;
	private static File file;
	private static byte[] arr;
	
	private static void copyStringToByteArray(String str, byte[] arr) {		
		
		for(int i = 0; i < str.length(); i++) {
			arr[i] = (byte)str.charAt(i);			
		}
	}
	
	@BeforeClass
	public static void method() {		
		
		peer = new PeerDHT[10];
		
        try {
            peer = createAndAttachPeersDHT(10, 7777);

            bootstrap(peer);      
            
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        fcc = new FileContentController(peer[4]);
        
        arr = new byte[str.length()];
		copyStringToByteArray(str,arr);		
	}
	
	
	
	@Test
	public void testCreateFile() {
				
		
		
		file = fcc.createFile("Random name", arr);
		
		//relevant objects have been created
		assertNotNull(file.getID());
		assertNotEquals(file.getBlocks().size(), 0);
		
		//number of blocks are corresponding to filesize and blocksize		
		if(file.getFileSize() % Constants.BLOCK_SIZE == 0) {
			assertEquals(file.getBlocks().size(), file.getFileSize()/Constants.BLOCK_SIZE);
		} else {
			assertEquals(file.getBlocks().size(), file.getFileSize()/Constants.BLOCK_SIZE+1);
		}
		
		//was distribution successful?
				
		
	}
	
	@Test
	public void testGetFileContent() {
		
		
		byte[] content = fcc.getFileContent(file);
		
		for(Number160 ID: file.getBlocks()) {
			
			Block block = fcc.getBlockDHT(ID);
			
			int length = block.getSize();
			
			//checking if block sizes make sense
			assertFalse(length != Constants.BLOCK_SIZE && length != file.getFileSize()%Constants.BLOCK_SIZE);
			
			//sequence number always needs to be smaller than the number of blocks, starting at 0 - n-1
			assertTrue(block.getSeq_number() < file.getBlocks().size());
			
			//hash needs to be set
			assertNotNull(block.getID());
			
			//calculating and checking checksums
			CRC32 crc32 = new CRC32();			
			crc32.update(block.getData());			
			assertEquals(crc32.getValue(), block.getChecksum());		
		}
		
	}
	
    public static PeerDHT[] createAndAttachPeersDHT(int nr, int port) throws IOException {
        
    	Random random = new Random();
    	
    	PeerDHT[] peers = new PeerDHT[nr];
        for (int i = 0; i < nr; i++) {
            if (i == 0) {
                peers[0] = new PeerBuilderDHT(new PeerBuilder(new Number160(random)).ports(port).start()).start();

            } else {
                peers[i] = new PeerBuilderDHT(new PeerBuilder(new Number160(random)).masterPeer(peers[0].peer()).start()).start();

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
