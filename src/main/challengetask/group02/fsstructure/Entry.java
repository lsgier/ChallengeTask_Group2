package challengetask.group02.fsstructure;

import net.tomp2p.peers.Number160;

import java.io.Serializable;

public class Entry implements Serializable, Cloneable {

    public enum TYPE {
    	FILE,
    	DIRECTORY, type;
    }
	
    protected Number160 ID;
    protected String entryName;
    protected TYPE type;
    protected long size;
    protected boolean dirtyBit;

    private static final long serialVersionUID = 1L;
    
    public Entry() {
    	
    }
    
    public boolean getDirtyBit() {
    	return dirtyBit;
    }

    public void setDirtyBit(boolean dirtyBit) {
        this.dirtyBit = dirtyBit;
    }

	public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public TYPE getType() {
        return type;
    }
    
    public void setID(Number160 ID) {
    	this.ID = ID;
    }

    public Number160 getID() {
        return ID;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getEntryName() {
        return entryName;
    }
}
