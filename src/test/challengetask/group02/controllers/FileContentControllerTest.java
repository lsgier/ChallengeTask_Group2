package challengetask.group02.controllers;

import static org.junit.Assert.*;

import java.util.zip.CRC32;

import net.tomp2p.peers.Number160;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import challengetask.group02.Constants;
import challengetask.group02.fsstructure.File;
import challengetask.group02.controllers.FileContentController;
import challengetask.group02.fsstructure.Block;

public class FileContentControllerTest {

	private String str = ("asdfaölskjfölkewj"
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
	
	private void copyStringToByteArray(String str, byte[] arr) {		
		
		for(int i = 0; i < str.length(); i++) {
			arr[i] = (byte)str.charAt(i);			
		}
	}
	
	@BeforeClass
	public static void method() {
		
		//create and bootstrap peers
		//put file into DHT
		
	}
	
	
	
	@Test
	public void testCreateFile() {
		
		FileContentController fcc = new FileContentController();
		
		byte[] arr = new byte[str.length()];
		copyStringToByteArray(str,arr);
		
		File file = fcc.createFile("Random name", arr);
		
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
		
		
		/*FileContentController fcc = new FileContentController();
		
		file = fcc.getFileContent(file);
		
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
		}*/
		
	}

}
