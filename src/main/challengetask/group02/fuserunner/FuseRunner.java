package challengetask.group02.fuserunner;

import challengetask.group02.controllers.ControllerContext;
import challengetask.group02.controllers.NoSuchFileOrDirectoryException;
import challengetask.group02.controllers.NotADirectoryException;
import challengetask.group02.fsstructure.Entry;
import net.fusejna.*;
import net.fusejna.types.TypeMode;
import net.fusejna.util.FuseFilesystemAdapterAssumeImplemented;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FuseRunner extends FuseFilesystemAdapterAssumeImplemented {
    private final String path;
    private ControllerContext controller;

    public FuseRunner(ControllerContext controller, String path) {
        this.controller = controller;
        this.path = path;
    }

    public void run() throws FuseException {
        try {
            this.log(true).mount(path);
        } catch (FuseException e) {
            e.printStackTrace();
        }
    }


    final String contents = "Hello World!\n";

    @Override
    public int getattr(final String path, final StructStat.StatWrapper stat) {
        try {
            Entry entry = controller.findEntry(path);
            if (entry.getType() == Entry.TYPE.DIRECTORY) {
                stat.setMode(TypeMode.NodeType.DIRECTORY);
                return 0;
            }
            if (entry.getType() == Entry.TYPE.FILE) {
                stat.setMode(TypeMode.NodeType.FILE);
                //stat.setMode(TypeMode.NodeType.FILE).size(contents.length());
            }


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
    public int read(final String path, final ByteBuffer buffer, final long size, final long offset, final StructFuseFileInfo.FileInfoWrapper info)
    {
        // Compute substring that we are being asked to read
        final String s = contents.substring((int) offset,
                (int) Math.max(offset, Math.min(contents.length() - offset, offset + size)));
        buffer.put(s.getBytes());
        return s.getBytes().length;
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
            System.out.println("Tried to treat this file as a directory: "+e.getMessage());
            return -ErrorCodes.ENOTDIR();
        } catch (NoSuchFileOrDirectoryException e) {
            System.out.println("Tried read this non-existing directory: " + e.getMessage());
            return -ErrorCodes.ENOENT();
        }


        return 0;
    }
}
