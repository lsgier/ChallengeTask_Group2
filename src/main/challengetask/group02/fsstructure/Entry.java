package challengetask.group02.fsstructure;

import net.tomp2p.peers.Number160;

/**
 * Created by melchior on 09.04.15.
 */
public class Entry {
    //ID might be unused. Why should a DHT entry need to contain its own Key?
    private Number160 ID;

    private Number160 ParentID;

    //This might also be unnecessary. If we store the EntryName in the Parent instead of in the object itself we can very quickly
    //find the ID of a desired Child. See my comment in the trello task "FS Performance" https://trello.com/c/5ECEngc9/27-fs-performance
    private String EntryName;

    public Entry(Number160 ID, Number160 parentID, String entryName) {
        this.ID = ID;
        ParentID = parentID;
        EntryName = entryName;
    }
    //TODO Object containing meta information

    public Number160 getID() {
        return ID;
    }

    public Number160 getParentID() {
        return ParentID;
    }

    public void setParentID(Number160 parentID) {
        ParentID = parentID;
    }

    public String getEntryName() {
        return EntryName;
    }



}
