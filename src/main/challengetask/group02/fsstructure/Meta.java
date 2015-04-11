package challengetask.group02.fsstructure;

import java.util.ArrayList;
import java.util.Date;

import net.tomp2p.peers.PeerAddress;

public class Meta {
	
	//some attributes are optional and might be used later on or not
	private Date creationDate;
	private Date lastModified;
	private PeerAddress owner;
	private long size; //in Bytes probably
	private ArrayList<Directory> path;

}
