package challengetask.group02.fsstructure;

import java.util.ArrayList;

import net.tomp2p.peers.Number160;

public class File extends Entry {

	private Number160 modifierPeer;
	private ArrayList<Number160> blocks;	
	private Meta meta;
	private boolean readOnly;

	private static final long serialVersionUID = 1L;
	final public static int BLOCK_SIZE =  16384*2;

	public File(String fileName, long fileSize, Number160 ID) {

		blocks = new ArrayList<>();
		meta = new Meta();
		
		readOnly = false;
		this.type = TYPE.FILE;		

		this.size = fileSize;
		this.entryName = fileName;
		
		
		this.ID = ID;
	}

	public File(Number160 ID, String entryName) {
		
		blocks = new ArrayList<>();
		meta = new Meta();
		
		readOnly = false;
		this.type = TYPE.FILE;		

		this.ID = ID;
		this.entryName = entryName;

		this.size = 0;
		
	}
	
	//empty constructor
	public File() {
		
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
		return meta.getAtime();
	}

	public void setAtime(long atime) {
		meta.setAtime(atime);
	}

	public long getCtime() {
		return meta.getCtime();
	}

	public void setCtime(long ctime) {
		meta.setCtime(ctime);
	}
}