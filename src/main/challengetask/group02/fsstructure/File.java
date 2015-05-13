package challengetask.group02.fsstructure;

import challengetask.group02.fsstructure.Entry;

import java.util.ArrayList;

import net.tomp2p.peers.Number160;

public class File extends Entry {

	private Number160 modifierPeer;
	private ArrayList<Number160> blocks;	
	private long atime;
	private long ctime;
	private boolean readOnly;

	private static final long serialVersionUID = 1L;

	//Like discussed, calculating and fetching data is done via controller classes	
	public File(String fileName, long fileSize, Number160 ID) {
		
		readOnly = false;
		this.type = TYPE.FILE;		

		this.size = fileSize;
		this.entryName = fileName;
		
		//this are the IDs of the blocks
		blocks = new ArrayList<Number160>();
		
		this.ID = ID;
	}

	public File(Number160 ID, Number160 parentID, String entryName) {
		
		readOnly = false;
		this.type = TYPE.FILE;		

		this.ID = ID;
		this.parentID = parentID;
		this.entryName = entryName;

		this.size = 0;
		
		blocks = new ArrayList<Number160>();

	}
	
	//empty constructor
	public File() {
		blocks = new ArrayList<Number160>();

	}
	
	public boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public Number160 getModifierPeer() {
		return modifierPeer;
	}

	public void setModifierPeer(Number160 modifierPeer) {
		this.modifierPeer = modifierPeer;
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
	
	public long getAtime() {
		return atime;
	}

	public void setAtime(long atime) {
		this.atime = atime;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}
}