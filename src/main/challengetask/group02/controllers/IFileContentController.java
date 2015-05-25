package challengetask.group02.controllers;

import challengetask.group02.controllers.exceptions.BusyException;
import challengetask.group02.controllers.exceptions.CRCException;
import challengetask.group02.fsstructure.File;

import java.nio.ByteBuffer;

public interface IFileContentController {
    int writeFile(File file, ByteBuffer buffer, long bufSize, long writeOffset) throws BusyException;
    byte[] readFile(File file, long size, long offset) throws CRCException;
}
