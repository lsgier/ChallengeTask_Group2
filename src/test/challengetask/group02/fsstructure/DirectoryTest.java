package challengetask.group02.fsstructure;

import net.tomp2p.peers.Number160;
import org.junit.Test;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DirectoryTest {



    Directory root = new Directory(Number160.createHash(0), null, "");

    @Test
    public void testAddingChildren() {
        root.addChild("home", Number160.createHash(1));
        root.addChild("etc", Number160.createHash(2));
        root.addChild("bin", Number160.createHash(3));
        root.addChild("dev", Number160.createHash(4));

        assertEquals(Number160.createHash(1), root.getChild("home"));
        assertEquals(Number160.createHash(2), root.getChild("etc"));
        assertEquals(Number160.createHash(3), root.getChild("bin"));
        assertEquals(Number160.createHash(4), root.getChild("dev"));
    }

    @Test
    public void duplicatesShouldOverwriteOldEntry() {
        root.addChild("home", Number160.createHash(1));
        root.addChild("home", Number160.createHash(7));
        assertEquals(Number160.createHash(7),root.getChild("home"));
    }


    //TODO test listing the content of root "/" and assert if added directories are in it

    //TODO test requesting a certain subderictory of root "/"

    //TODO test what happens if a nonexisting subdirectory is requested

}
