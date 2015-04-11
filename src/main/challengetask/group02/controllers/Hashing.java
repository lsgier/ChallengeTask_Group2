package challengetask.group02.controllers;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import net.tomp2p.peers.Number160;

//creating a static class for hashing, can be used for everything: directories, files, blocks
public final class Hashing {
	
	public static Number160 generateHash(String fileName) {
		
		Number160 ID = new Number160();
		
		//generate a timestamp
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		
		Timestamp currentTimestamp = new Timestamp(now.getTime());		
		
		return ID;		
	}

}
