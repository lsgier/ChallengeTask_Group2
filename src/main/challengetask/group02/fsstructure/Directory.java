package challengetask.group02.fsstructure;

import net.tomp2p.peers.Number160;

import java.util.Hashtable;

public class Directory extends Entry{

    private Hashtable<String, Number160> fileChildren;
    private Hashtable<String, Number160> dirChildren;

    private static final long serialVersionUID = 1L;
    
    public Directory(Number160 ID, String entryName) {
        this.type = TYPE.DIRECTORY;

        this.ID = ID;
        this.entryName = entryName;

        fileChildren = new Hashtable<>();
        dirChildren = new Hashtable<>();

    }

    //Distinguishing between files and directories    
    public void addChild(String name, Number160 ID, TYPE type) {
    	if(type.equals(TYPE.FILE)) {
    		fileChildren.put(name, ID);
    	} else if (type.equals(TYPE.DIRECTORY)) {
    		dirChildren.put(name, ID);
    	}        
    }

    public Number160 getChild(String name) {
        return getChildren().get(name);
    }
    

    public Number160 getChild(String name, TYPE type) {
        
    	if(type.equals(TYPE.FILE)){
    		return fileChildren.get(name);
        	
        } else if(type.equals(TYPE.DIRECTORY)) {
        	return dirChildren.get(name);
        }    	
    	return null;
    }

    public Hashtable<String, Number160> getChildren() {
        Hashtable<String, Number160> allChildren = new Hashtable<>();
        allChildren.putAll(fileChildren);
        allChildren.putAll(dirChildren);
        return allChildren;
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

    public void renameChild(String oldName, String newName) {
        if (fileChildren.containsKey(oldName)) {
            Number160 value = fileChildren.remove(oldName);
            fileChildren.put(newName, value);
        }
        if (dirChildren.containsKey(oldName)) {
            Number160 value = dirChildren.remove(oldName);
            dirChildren.put(newName, value);
        }
    }

    public void removeChild(String name) {
        fileChildren.remove(name);
        dirChildren.remove(name);
    }
}