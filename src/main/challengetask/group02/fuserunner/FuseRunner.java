package challengetask.group02.fuserunner;

import challengetask.group02.controllers.*;
import net.fusejna.*;
import net.fusejna.StructFuseFileInfo.FileInfoWrapper;
import net.fusejna.types.TypeMode;
import net.fusejna.util.FuseFilesystemAdapterAssumeImplemented;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class FuseRunner extends FuseFilesystemAdapterAssumeImplemented {
    private final String path;
    private ControllerContext controller;


    public FuseRunner(ControllerContext controller, String path) {
        this.controller = controller;
        this.path = path;
    }

    public void run(Logger logger) throws FuseException {
        try {
            this.log(logger).mount(path);
        } catch (FuseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getattr(final String path, final StructStat.StatWrapper stat) {
        try {
            controller.updateFileMetaData(path, stat);

            return 0;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NotADirectoryException e) {
            System.out.println("Tried to treat this file as a directory: " + e.getMessage());
            return -ErrorCodes.ENOTDIR();
        } catch (NoSuchFileOrDirectoryException e) {
            System.out.println("Tried get attributes of this non-existing file: " + e.getMessage());
            return -ErrorCodes.ENOENT();
        }

        return -ErrorCodes.ENOENT();
    }

    @Override
    public int read(final String path, final ByteBuffer buffer, final long size, final long offset, final StructFuseFileInfo.FileInfoWrapper info) {
        // Compute substring that we are being asked to read
        byte[] s = new byte[0];
        try {
            s = controller.readFile(path, size, offset);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NotADirectoryException e) {
            //TODO return fuse error codes in these catch phrases
            e.printStackTrace();
        } catch (NotAFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchFileOrDirectoryException e) {
            e.printStackTrace();
        }
        buffer.put(s);
        return s.length;
    }

    @Override
    public int readdir(final String path, final DirectoryFiller filler) {
        try {
            for (String child : controller.readDir(path)) {
                filler.add(child);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NotADirectoryException e) {
            System.out.println("Tried to treat this file as a directory: " + e.getMessage());
            return -ErrorCodes.ENOTDIR();
        } catch (NoSuchFileOrDirectoryException e) {
            System.out.println("Tried read this non-existing directory: " + e.getMessage());
            return -ErrorCodes.ENOENT();
        }
        return 0;
    }

    @Override
    public int mkdir(final String path, final TypeMode.ModeWrapper mode) {
        try {
            controller.createDir(path);
            //mode.setMode(TypeMode.NodeType.DIRECTORY);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NotADirectoryException e) {
            System.out.println("Tried to treat this file as a directory: " + e.getMessage());
            return -ErrorCodes.ENOTDIR();
        } catch (NoSuchFileOrDirectoryException e) {
            System.out.println("Tried read this non-existing directory: " + e.getMessage());
            return -ErrorCodes.ENOENT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int create(final String path, final TypeMode.ModeWrapper mode, final StructFuseFileInfo.FileInfoWrapper info) {
        try {
            controller.createFile(path);
            //mode.setMode(TypeMode.NodeType.DIRECTORY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int rename(final String path, final String newName) {
        try {
            controller.rename(path, newName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int rmdir(final String path) {
        try {
            controller.deleteDirectory(path);
        } catch (DirectoryNotEmptyException e) {
            return -ErrorCodes.ENOTEMPTY();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int unlink(final String path) {
        try {
            controller.deleteFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (NotAFileException e) {
            //TODO fuse errors

            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int write(final String path, final ByteBuffer buf, final long bufSize, final long writeOffset,
                     final StructFuseFileInfo.FileInfoWrapper wrapper) {
        try {
            return controller.writeFile(path, buf, bufSize, writeOffset);
        } catch (BusyException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NotADirectoryException e) {
            //TODO return fuse errors
            e.printStackTrace();
        } catch (NotAFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchFileOrDirectoryException e) {
            e.printStackTrace();
        }
        //return ((MemoryFile) p).write(buf, bufSize, writeOffset);

        return 0;
    }

    @Override
    public int flush(final String path, final FileInfoWrapper info) {

        //introduced for the locking mechanism
        //flush is only used in context with file
        try {
            controller.whenFileClosed(path);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NotADirectoryException e) {
            //TODO create method that handle exceptions and returns the right error code
            e.printStackTrace();
        } catch (NoSuchFileOrDirectoryException e) {
            e.printStackTrace();
        } catch (NotAFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
