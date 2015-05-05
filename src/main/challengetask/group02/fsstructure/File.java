package challengetask.group02.fsstructure;

import challengetask.group02.fsstructure.Entry;

import java.util.ArrayList;

import net.tomp2p.peers.Number160;

public class File extends Entry {
	
	private long fileSize;
	private Meta meta;

	private ArrayList<Number160> blocks;	
	
	//Like discussed, calculating and fetching data is done via controller classes	
	public File(String fileName, long fileSize, Number160 ID) {
		
		//this is for the dirtybit
		super();
		
		this.type = TYPE.FILE;

		this.fileSize = fileSize;
		this.entryName = fileName;
		
		//this are the IDs of the blocks
		blocks = new ArrayList<Number160>();
		
		this.ID = ID;
	}

	public File(Number160 ID, Number160 parentID, String entryName) {
		
		//this is for the dirtybit
		super();
		
		this.type = TYPE.FILE;

		this.ID = ID;
		this.parentID = parentID;
		this.entryName = entryName;

		this.fileSize = 0;
		
		blocks = new ArrayList<Number160>();

	}
	
	//empty constructor
	public File() {
		blocks = new ArrayList<Number160>();

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
	
	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}	
}