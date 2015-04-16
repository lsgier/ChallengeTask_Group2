package challengetask.group02.controllers;

import net.tomp2p.peers.Number160;
import challengetask.group02.fsstructure.File;

//This class is used to split up the files and also fetch them
public class FileContentController {
	
	
	//Not sure if byte[] is the best choice, Object might be better, depends on TomP2P
	//Just began here, more to come the next few days
	public File createFile(String fileName, byte[] content) {
		
		int dataSize = content.length;
		
		//To create a new file we have to create a hash
		//Number160 ID = Hashing.generateHash(fileName);
				
		File tmp = new File();
		
		//splitting the file apart and create blocks
		while (dataSize >= 0) {
					
			
		}		
		
		return tmp;		
	}	
	

	
	
}
