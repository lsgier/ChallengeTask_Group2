package challengetask.group02.fuserunner;

import challengetask.group02.controllers.ControllerContext;
import net.fusejna.*;
import net.fusejna.types.TypeMode;
import net.fusejna.util.FuseFilesystemAdapterAssumeImplemented;

import java.io.File;
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


    final String filename = "/hello.txt";
    final String contents = "Hello World!\n";

    @Override
    public int getattr(final String path, final StructStat.StatWrapper stat)
    {
        if (path.equals(File.separator)) { // Root directory
            stat.setMode(TypeMode.NodeType.DIRECTORY);
            return 0;
        }
        if (path.equals(filename)) { // hello.txt
            stat.setMode(TypeMode.NodeType.FILE).size(contents.length());
            return 0;
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
    public int readdir(final String path, final DirectoryFiller filler)
    {
        filler.add(filename);
        return 0;
    }
}
