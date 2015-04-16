package challengetask.group02.fsstructure;

import challengetask.group02.fsstructure.Entry;
import java.util.ArrayList;

public class File extends Entry {
	
	private boolean completeFlag;
	private ArrayList<Block> blocks;	
	
	//Like discussed, calculating and fetching data is done via controller classes
	
	public File() {
		
		completeFlag = false;
		
	}
	
	public void setBlocks(ArrayList<Block> blocks) {
		
		
	}
	
	public ArrayList<Block> getBlocks() {
		
		return blocks;		
	}
	
	
	
	
}