package challengetask.group02.fsstructure;


import challengetask.group02.fsstructure.Entry;
import net.tomp2p.peers.Number160;

import java.util.Hashtable;

public class Directory extends Entry{

    private Hashtable<String, Number160> children;

    public Directory(Number160 ID, Number160 parentID, String entryName) {
        this.ID = ID;
        this.parentID = parentID;
        this.entryName = entryName;

        children = new Hashtable<String, Number160>();
    }
}
