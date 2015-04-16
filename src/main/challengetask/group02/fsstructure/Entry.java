package challengetask.group02.fsstructure;

import net.tomp2p.peers.Number160;

import java.io.Serializable;

public class Entry implements Serializable {

    public enum TYPE {
    	FILE,
    	DIRECTORY;
    }
	
    protected Number160 ID;
    protected Number160 parentID;
    protected String entryName;
    protected Meta meta;
    protected TYPE type;

    public TYPE getType() {
        return type;
    }

    public Number160 getID() {
        return ID;
    }

    public Number160 getParentID() {
        return parentID;
    }

    public void setParentID(Number160 parentID) {
        //avoid that the root directory (has no parent) can be moved to a subdirectory which would mean that the entry-point to the fs is lost.
        if(this.parentID ==null) {
            //TODO throw exception?
            return;
        }

        else {
            this.parentID = parentID;
        }
    }

    public void setEntryName(String entryName) {
        //TODO implement DHT request (or use challengetask.group02.controllers) to change the name of this object in the parent objects children list.
        this.entryName = entryName;
    }

    public String getEntryName() {
        //TODO implement DHT request (or use challengetask.group02.controllers) to find out name
        return entryName;
    }




}
