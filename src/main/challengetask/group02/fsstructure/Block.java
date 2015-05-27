package challengetask.group02.fsstructure;

import java.io.Serializable;
import net.tomp2p.peers.Number160;

public class Block implements Serializable {
		
	private Number160 ID;
	//used to identify the position of a block within a file
	private long seq_number; 
	//used for error detection
	//private long checksum;
	private int size;
	private byte[] data;

	private static final long serialVersionUID = 1L;

	public Block(Number160 ID, long seq_number, int size, byte[] data) {
		
		this.ID = ID;
		this.seq_number = seq_number;
		//this.checksum = checksum;
		this.data = data;	
		this.size = size;
	}		
	
	//empty constructor
	public Block() {
		data = new byte[File.BLOCK_SIZE];
	}	
	
	public Number160 getID() {
		return ID;
	}

	public void setID(Number160 iD) {
		ID = iD;
	}

	public long getSeq_number() {
		return seq_number;
	}

	public void setSeq_number(long seq_number) {
		this.seq_number = seq_number;
	}

	/*public long getChecksum() {
		return checksum;
	}*/

	/*public void setChecksum(long checksum) {
		this.checksum = checksum;
	}*/

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}