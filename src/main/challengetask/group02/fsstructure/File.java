package challengetask.group02.fsstructure;

import challengetask.group02.fsstructure.Entry;

import java.util.ArrayList;

import net.tomp2p.peers.Number160;

public class File extends Entry {
	
	private long fileSize;
	private ArrayList<Number160> blocks;	
	
	//Like discussed, calculating and fetching data is done via controller classes
	
	public File(String fileName, long fileSize, Number160 ID) {
		this.type = TYPE.FILE;

		this.fileSize = fileSize;
		this.entryName = fileName;
		
		//this are the IDs of the blocks
		blocks = new ArrayList<Number160>();
		
		this.ID = ID;
	}

	
	//empty constructor
	public File() {

	}
	
	public void setFileSize(long fileSize) {
		
		this.fileSize = fileSize;
	}
	
	public long getFileSize() {
		
		return fileSize;
	}
	
	public void addBlock(Number160 block) {
		
		blocks.add(block);
	}
	
	public void setBlocks(ArrayList<Number160> blocks) {
		this.blocks = blocks;		
	}
	
	public ArrayList<Number160> getBlocks() {
		
		return blocks;		
	}

	
}