package challengetask.group02.controllers;

import java.util.Random;
import java.util.zip.CRC32;

import net.tomp2p.peers.Number160;
import challengetask.group02.fsstructure.Block;
import challengetask.group02.fsstructure.File;
import challengetask.group02.Constants;

//This class is used to split up the files and also fetch them
public class FileContentController {
		
	//Not sure if byte[] is the best choice, Object might be better, depends on TomP2P
	//Just began here, more to come the next few days
	public File createFile(String fileName, byte[] content) {
		
		int dataSize = content.length;

		//used for hashing
		Random random = new Random();				
		File file = new File(dataSize);
		
		//this is used for the checksum calculation
		CRC32 crc32 = new CRC32();
		
		int seq_number = 0;	
		int position = 0;
		long crc;
		
		//splitting the file apart and create blocks
		while (dataSize > 0) {
			
			//generate a new Hash for the new Block
			Number160 ID = new Number160(random);
						
			if(dataSize >= Constants.BLOCK_SIZE) {
				
				byte[] blockData = new byte[Constants.BLOCK_SIZE];
				
				System.arraycopy(content, position, blockData, 0, Constants.BLOCK_SIZE);
				crc32.update(blockData);
				crc = crc32.getValue();
				
				Block block = new Block(ID, seq_number, crc, Constants.BLOCK_SIZE, blockData);
				
				file.addBlock(block);
				
				position +=	Constants.BLOCK_SIZE;
				dataSize -= Constants.BLOCK_SIZE;
			} else {
				//last chunk
				byte[] blockData = new byte[dataSize];
				
				System.arraycopy(content,  position,  blockData, 0, dataSize);
				crc32.update(blockData);
				crc = crc32.getValue();
				
				Block block = new Block(ID, seq_number, crc, dataSize, blockData);
				file.addBlock(block);
				
				position += dataSize;
				dataSize = 0;				
			}
			
			seq_number++;			
		}		
		
		return file;		
	}	
	

	
	
}
