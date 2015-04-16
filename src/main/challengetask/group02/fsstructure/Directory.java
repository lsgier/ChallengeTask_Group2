package challengetask.group02.fsstructure;

import net.tomp2p.peers.Number160;

import java.util.Hashtable;

public class Directory extends Entry{

    private Hashtable<String, Number160> fileChildren;
    private Hashtable<String, Number160> dirChildren;
    
    public Directory(Number160 ID, Number160 parentID, String entryName) {
        this.type = TYPE.DIRECTORY;

        this.ID = ID;
        this.parentID = parentID;
        this.entryName = entryName;

        fileChildren = new Hashtable<String, Number160>();
        dirChildren = new Hashtable<String, Number160>();

    }

    //Distinguishing between files and directories    
    public void addChild(String entryName, Number160 ID, TYPE type) {
    	
    	if(type.equals(TYPE.FILE)) {
    		fileChildren.put(entryName, ID);    	
    	} else if (type.equals(TYPE.DIRECTORY)) {
    		dirChildren.put(entryName, ID);
    	}        
    }
    

    public Number160 getChild(String entryName, TYPE type) {
        
    	if(type.equals(TYPE.FILE)){
    		return fileChildren.get(entryName);
        	
        } else if(type.equals(TYPE.DIRECTORY)) {
        	return dirChildren.get(entryName);
        }    	
    	return null;
    }

    //Overloading
    public Hashtable<String, Number160> getChildren(TYPE type) {
        
    	if(type.equals(TYPE.FILE)) {
    		return fileChildren;    		
    	} else if(type.equals(TYPE.DIRECTORY)) {
    		return dirChildren;
    	}    	
    	
    	return null;
    }
    
    
    public void removeChildren() {
    	
    	fileChildren.clear();
    	dirChildren.clear();
    }
}







