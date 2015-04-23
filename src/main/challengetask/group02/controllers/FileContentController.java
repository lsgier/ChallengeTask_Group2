package challengetask.group02.controllers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.zip.CRC32;

import net.tomp2p.dht.FutureDHT;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
import challengetask.group02.fsstructure.Block;
import challengetask.group02.fsstructure.File;
import challengetask.group02.Constants;

//This class is used to split up the files and also fetch them
public class FileContentController {
	
	private PeerDHT peer;
	
	public FileContentController(PeerDHT peer) {
		
		this.peer = peer;
	}
		
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
				//TODO throw ChecksumException();
			}
			
			if(seqNumber != block.getSeq_number()) {
				//TODO throw SeqNumberException();
			}
			
			System.arraycopy(block.getData(), 0, content, position, block.getSize());
			position += block.getSize();			
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

	public byte[] readFile(String path, long size, long offset) {
		return "xxx".getBytes();
	}

	public int writeFile(ByteBuffer buf, long bufSize, long writeOffset) {
		return 0; //the size of the content that was written
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
	}
}
