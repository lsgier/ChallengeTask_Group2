package challengetask.group02.fsstructure;

import challengetask.group02.fsstructure.Entry;
import java.util.ArrayList;

public class File extends Entry {
	
	private boolean completeFlag;
	private long fileSize;
	private ArrayList<Block> blocks;	
	
	//Like discussed, calculating and fetching data is done via controller classes
	
	public File(long fileSize) {
		this.type = TYPE.FILE;

		completeFlag = false;
		this.fileSize = fileSize;
		blocks = new ArrayList<Block>();

	}
	
	public void setFileSize(long fileSize) {
		
		this.fileSize = fileSize;
	}
	
	public long getFileSize() {
		
		return fileSize;
	}
	
	public void addBlock(Block block) {
		
		blocks.add(block);
	}
	
	public void setBlocks(ArrayList<Block> blocks) {
		
		
	}
	
	public ArrayList<Block> getBlocks() {
		
		return blocks;		
	}
	
	
	
	
}