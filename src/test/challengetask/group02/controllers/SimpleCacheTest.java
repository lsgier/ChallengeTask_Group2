package challengetask.group02.controllers;

import challengetask.group02.fsstructure.Directory;
import challengetask.group02.fsstructure.Entry;
import challengetask.group02.helpers.SimpleCache;
import net.tomp2p.peers.Number160;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleCacheTest {

    @Test
    public void testCache() {
        SimpleCache<Entry> cache = new SimpleCache<>(1);

        Directory dir = new Directory(Number160.ZERO, "test");

        assertEquals(null, cache.get("/test"));

        cache.put("/test", dir);

        assertEquals(dir, cache.get("/test"));

    }

}