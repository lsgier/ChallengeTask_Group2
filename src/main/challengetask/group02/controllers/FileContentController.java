package challengetask.group02.controllers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.CRC32;

import net.tomp2p.dht.FutureDHT;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
import challengetask.group02.fsstructure.Block;
import challengetask.group02.fsstructure.File;
import challengetask.group02.helpers.DHTPutGetHelper;
import challengetask.group02.Constants;

//This class is used to split up the files and also fetch them
public class FileContentController {
	
	private PeerDHT peer;
	private DHTPutGetHelper dhtPutGetHelper;
	
	public FileContentController(PeerDHT peer) {
		
		this.peer = peer;
		dhtPutGetHelper = new DHTPutGetHelper(this.peer);
	}
	
	
	public int writeFile(File file, ByteBuffer buffer, long bufSize, long writeOffset) {
		
		Random random = new Random();
		
		byte[] content = new byte[(int) bufSize];
		long fileSize = file.getFileSize();
		
		int startBlock = (int)(writeOffset/Constants.BLOCK_SIZE);
		int endBlock = (int)((bufSize+writeOffset-1)/Constants.BLOCK_SIZE);
				
		//copy the bytebuffer (data to write) into our content array
		//assuming we the content has length bufSize, otherwise BufferUnderFlowException will be thrown
		buffer.get(content, 0, (int) bufSize);
		ArrayList<Number160> blockIDs = file.getBlocks();	

		//first block is special case
		int startBytes = 0;
		//last block is special case
		int endBytes = 0;
		//maintains the pointer where to read/write
		int positon = 0;
		
		for(int index = startBlock; index <= endBlock; index++) {
			
			//startBytes = Constants.BLOCK_SIZE - (int)offset%Constants.BLOCK_SIZE;
			Block block;
			int bytesToWrite = 0;
			
			
			//if the block doesn't exist, create a new one
			if(index > blockIDs.size()-1) {
				
				Number160 ID = new Number160(random);				
				//block = new Block(...);				
			} else {
				//if the block exists, fetch it
				block = getBlockDHT(blockIDs.get(index)); 
				
			}			
			
			//we have to make a distinction between which block we are visiting at the moment
			if(startBlock == endBlock) {
				bytesToWrite = (int)bufSize;				
			} else {				
				if(index == startBlock) {		
					startBytes = Constants.BLOCK_SIZE - (int)writeOffset%Constants.BLOCK_SIZE;
					bytesToWrite = 0;
				} else if(index == endBlock) {					
					
					
					
				} else {					
					
					
				}						
			}
		}
		
		return 0; //the size of the content that was written
	}
	
	/*
	private int write(final ByteBuffer buffer, final long bufSize, final long writeOffset)
	{
		final int maxWriteIndex = (int) (writeOffset + bufSize);
		final byte[] bytesToWrite = new byte[(int) bufSize];
		synchronized (this) {
			if (maxWriteIndex > contents.capacity()) {
				// Need to create a new, larger buffer
				final ByteBuffer newContents = ByteBuffer.allocate(maxWriteIndex);
				newContents.put(contents);
				contents = newContents;
			}
			buffer.get(bytesToWrite, 0, (int) bufSize);
			contents.position((int) writeOffset);
			contents.put(bytesToWrite);
			contents.position(0); // Rewind
		}
		return (int) bufSize;
	}
	 */

	
		
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
	
	
	public byte[] readFile(File file, long size, long offset) {
		
		//The following is the function definition by FUSE, instead of the String Path
		//we take as an argument the file, which is being determined by the TreeController
		//readFile(String path, long size, long offset)
		
		//We don't need to read the whole file, but only "size" bytes, starting from "offset"
		byte[] content = new byte[(int)size];
		
		ArrayList<Number160> blocks = file.getBlocks();
		
		//evaulate which block offset points to we assume the first block has index 0
		//the first block spans from 0 - BLOCK_SIZE-1
		//the second block from BLOCK_SIZE - 2*BLOCK_SIZE-1 etc.
		int startBlock = (int)(offset/Constants.BLOCK_SIZE);
		
		//how many blocks to read? depends on the position of the offset
		//the -1 because the byte where the offset points to is read as well
		//example offset "abcdefgh" with offset = 3 and length = 3 is "def"
		int endBlock = (int)((size+offset-1)/Constants.BLOCK_SIZE);
		
		//number of bytes read from the first block
		int startBytes = 0;
		//number of bytes read from the last block
		int endBytes = 0;
		int position = 0;
		
		for(int index = startBlock; index <= endBlock; index++) {
			
			Number160 ID = blocks.get(index);
			Block block = getBlockDHT(ID);
			CRC32 crc32 = new CRC32();
			int bytesToWrite = 0;
			
			//first check if crc is correct
			crc32.update(block.getData());
			if(! (crc32.getValue() == block.getChecksum()) ) {
				//this needs to be checked or an appropriate excpetion needs to be thrown
				return null;
			}
			
			//since we read sequential, sequence number checking doesn't make sense in this case
			
			//we have to do case distinction, if we only have one block to read, it's easily done.
			if(startBlock == endBlock) {
				bytesToWrite = (int)size;
			} else {
				
				//if it's the first block we're reading, we only have to start to read from offset
				//last block is also special
				if(index == startBlock) {
				
					startBytes = Constants.BLOCK_SIZE - (int)offset%Constants.BLOCK_SIZE;
					bytesToWrite = startBytes;
				} else if(index == endBlock) {
					
					endBytes = ((int)size - startBytes)%Constants.BLOCK_SIZE;
					bytesToWrite = endBytes;
				} else {
					bytesToWrite = Constants.BLOCK_SIZE;
				}
			}
			
			System.arraycopy(block.getData(), (int)offset%Constants.BLOCK_SIZE, content,  position,  bytesToWrite);
			offset = 0;
			position += bytesToWrite;
		}		
		
		return content;	
	}
	
	//DHT FUNCTIONALITY --------------------------------------------------------
	
	//overloaded methods, invoked differently depending on File or Block argument
	public void putIntoDHT(Number160 ID, File file) {
		
		Data data;
		
		try {
			data = new Data(file);
			FutureDHT futureDHT = peer.put(ID).data(data).start();
	        futureDHT.awaitUninterruptibly();	
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void putIntoDHT(Number160 ID, Block block) {
		
		Data data;
		
		try {
			data = new Data(block);
			FutureDHT futureDHT = peer.put(ID).data(data).start();
			futureDHT.awaitUninterruptibly();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public Block getBlockDHT(Number160 ID) {
		
		Block block;
		
		try {			
			FutureGet futureGet = peer.get(ID).start();
			futureGet.awaitUninterruptibly();
			
			block = (Block)futureGet.data().object();
			
			return block;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
		return null;
	}	
	
	public PeerDHT getPeer() {
		return peer;
	}

	public void setPeer(PeerDHT peer) {
		this.peer = peer;
	}

}
