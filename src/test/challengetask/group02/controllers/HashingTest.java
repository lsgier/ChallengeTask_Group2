package challengetask.group02.controllers;

import static org.junit.Assert.*;
import junit.framework.Assert;
import net.tomp2p.peers.Number160;

import org.junit.Test;

public class HashingTest {

	@Test
	public void testGenerateHash() {
		
		Number160 ID = Hashing.generateHash("SampleString.txt");
		
		//this assures that an ID has generated
		assertNotNull(ID);
	}

}
