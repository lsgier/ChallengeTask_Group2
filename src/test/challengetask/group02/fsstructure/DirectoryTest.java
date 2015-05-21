package challengetask.group02.fsstructure;


import challengetask.group02.fsstructure.Entry.TYPE;

import net.tomp2p.peers.Number160;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DirectoryTest {



    Directory root = new Directory(Number160.createHash(0), "");

    @Test
    public void testAddingChildren() {
        root.addChild("home", Number160.createHash(1), TYPE.DIRECTORY);
        root.addChild("etc", Number160.createHash(2), TYPE.DIRECTORY);
        root.addChild("bin", Number160.createHash(3), TYPE.DIRECTORY);
        root.addChild("dev", Number160.createHash(4), TYPE.DIRECTORY);

        assertEquals(Number160.createHash(1), root.getChild("home", TYPE.DIRECTORY));
        assertEquals(Number160.createHash(2), root.getChild("etc", TYPE.DIRECTORY));
        assertEquals(Number160.createHash(3), root.getChild("bin", TYPE.DIRECTORY));
        assertEquals(Number160.createHash(4), root.getChild("dev", TYPE.DIRECTORY));
    }

    @Test
    public void duplicatesShouldOverwriteOldEntry() {
        root.addChild("home", Number160.createHash(1), TYPE.DIRECTORY);
        root.addChild("home", Number160.createHash(7), TYPE.DIRECTORY);
        assertEquals(Number160.createHash(7),root.getChild("home", TYPE.DIRECTORY));
    }
    
    @Test
    public void distinguishFilesDirectories() {
    	
    	root.removeChildren();
    	
    	root.addChild("file.txt", Number160.createHash(100), TYPE.FILE);
    	
    	assertTrue(root.getChildren(TYPE.FILE).size() != 0);
    	assertTrue(root.getChildren(TYPE.DIRECTORY).size() == 0);
    }

    @Test
    public void testRenamingChild() {
        String oldName = "child";
        String newName = "childWithNewName";
        Number160 childID = Number160.createHash(37829);

        Directory child = new Directory(childID, oldName);
        root.addChild(child.getEntryName(), child.getID(), child.getType());

        root.renameChild(oldName, newName);

        assertEquals(childID, root.getChild(newName));

    }
}
