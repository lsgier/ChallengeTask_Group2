package challengetask.group02.controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import challengetask.group02.fsstructure.File;
import challengetask.group02.controllers.FileContentController;

public class FileContentControllerTest {

	private String str = ("asdfaölskjfölkewj"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf"
			+ "ölwkejfölkjdsföljweölfjwölekfjölwkejfölkwejfölkw"
			+ "söldjfökeföljeölksjfölkajwöelkjföalkjefölakwjeöf");
	
	private void copyStringToByteArray(String str, byte[] arr) {		
		
		for(int i = 0; i < str.length(); i++) {
			arr[i] = (byte)str.charAt(i);			
		}
	}
	
	@Test
	public void testCreateFile() {
		
		FileContentController fcc = new FileContentController();
		
		byte[] arr = new byte[str.length()];
		copyStringToByteArray(str,arr);
		
		File file = fcc.createFile("Random name", arr);
		System.out.println(file.getFileSize());
		System.out.println(file.getID());
		
		
	}

}
