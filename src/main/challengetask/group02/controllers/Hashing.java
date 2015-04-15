package challengetask.group02.controllers;

import net.tomp2p.peers.Number160;

//creating a static class for hashing, can be used for everything: directories, files, blocks
public final class Hashing {
	
	public static final int STRING_LIMIT = 42;
	public static final int TIME_PREFIX = 8;
	
	public static Number160 generateHash(String fileName) {
		
		Number160 ID;
		String timeString, toHash, fileHex = "";
		long timestamp = System.currentTimeMillis();
		
		//wasting space if we choose a timestamp too large, there's a string limit for Number160
		timeString = Long.toString(timestamp);
		timeString = timeString.substring(0, TIME_PREFIX);
		
		//We need to convert the fileName into Hex, TomP2P demands it
		int len = fileName.length();
		int count = 0;
		while(count < len) {
			char tmp = fileName.charAt(count);
			int a = (int)tmp;
			fileHex += Integer.toHexString(a);		
			count++;
		}		
		
		//in case the string is too large to stuff into Number160
		if(fileHex.length() > (STRING_LIMIT - TIME_PREFIX)) {
			throw new IllegalArgumentException("String size too long");
		}
		
		//Requirement to be hashable
		toHash = "0x"+timeString+fileHex;
		
		ID = new Number160(toHash);			
		
		return ID;		
	}

}
