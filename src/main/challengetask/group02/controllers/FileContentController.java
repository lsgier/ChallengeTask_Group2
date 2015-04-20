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
		
		//create a hash for the file itself
		Number160 fileID = new Number160(random);
		
		File file = new File(fileName, dataSize, fileID);
				
		int seqNumber = 0;	
		int position = 0;
		long crc;
		
		//splitting the file apart and create blocks
		while (dataSize > 0) {
			
			//generate a new Hash for the new Block
			Number160 ID = new Number160(random);
						
			if(dataSize >= Constants.BLOCK_SIZE) {
				
				byte[] blockData = new byte[Constants.BLOCK_SIZE];
				
				System.arraycopy(content, position, blockData, 0, Constants.BLOCK_SIZE);
				
				//this is used for the checksum calculation
				CRC32 crc32 = new CRC32();
				
				crc32.update(blockData);
				crc = crc32.getValue();
				
				Block block = new Block(ID, seqNumber, crc, Constants.BLOCK_SIZE, blockData);				
				file.addBlock(block.getID());
				
				//save the block in the DHT
				putIntoDHT(block.getID(), block);
				
				position +=	Constants.BLOCK_SIZE;
				dataSize -= Constants.BLOCK_SIZE;
			} else {
				//last chunk
				byte[] blockData = new byte[dataSize];
				
				System.arraycopy(content,  position,  blockData, 0, dataSize);
				//this is used for the checksum calculation
				CRC32 crc32 = new CRC32();
				
				crc32.update(blockData);
				crc = crc32.getValue();
				
				Block block = new Block(ID, seqNumber, crc, dataSize, blockData);
				file.addBlock(block.getID());
				
				//save the block in the DHT
				putIntoDHT(block.getID(), block);
				
				position += dataSize;
				dataSize = 0;				
			}
			
			seqNumber++;			
		}		
		
		//file goes into DHT at last
		putIntoDHT(file.getID(), file);
		
		return file;		
	}	
	
	
	//I suppose the file object is already retrieved by other controllers
	//the argument here is thus File, not Number160
	public byte[] getFileContent(File file) {
		
		//There's still room for performance boosts with using Threads
		byte[] content = new byte[(int) file.getFileSize()];
		//since this is an int, we can only store 2^32 Byte (4.096 GB) per file
		int position = 0;
		int seqNumber = 0;
		
		//fetch the IDs
		for(Number160 ID: file.getBlocks()) {
			
			Block block = getBlockDHT(ID);
			
			//Calculating the CRC to check data integrity
			CRC32 crc32 = new CRC32();
			crc32.update(block.getData());
			
			if(crc32.getValue() != block.getChecksum()) {
				//throw ChecksumException();
			}
			
			if(seqNumber != block.getSeq_number()) {
				//throw SeqNumberException();
			}
			
			System.arraycopy(block, 0, content, position, block.getSize());
			position += block.getSize();			
		}		
		
		return content;		
	}
	
	
	//DHT FUNCTIONALITY --------------------------------------------------------
	
	//overloaded methods, invoked differently depending on File or Block argument
	public void putIntoDHT(Number160 ID, File file) {
		
		
		//will be completed later
		
		
	}
	
	public void putIntoDHT(Number160 ID, Block block) {
		
		
	}
	
	public Block getBlockDHT(Number160 ID) {
		
		Block block = new Block();
		
		return block;
	}	
	
}
