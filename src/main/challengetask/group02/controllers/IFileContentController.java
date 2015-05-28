package challengetask.group02.controllers;

import challengetask.group02.controllers.exceptions.BusyException;
import challengetask.group02.controllers.exceptions.CRCException;
import challengetask.group02.fsstructure.File;
import challengetask.group02.helpers.SimpleCache;

import java.nio.ByteBuffer;

public interface IFileContentController {
    int writeFile(File file, ByteBuffer buffer, long bufSize, long writeOffset, SimpleCache<File> cache) throws BusyException;
    byte[] readFile(File file, long size, long offset) throws CRCException;

    void flush(String path, File file);
}
