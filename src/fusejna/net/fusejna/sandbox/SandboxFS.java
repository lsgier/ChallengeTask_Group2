package net.fusejna.sandbox;

import net.fusejna.DirectoryFiller;
import net.fusejna.ErrorCodes;
import net.fusejna.FuseException;
import net.fusejna.StructFuseFileInfo.FileInfoWrapper;
import net.fusejna.StructStat.StatWrapper;
import net.fusejna.types.TypeMode.NodeType;
import net.fusejna.util.FuseFilesystemAdapterFull;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class SandboxFS extends FuseFilesystemAdapterFull
{
    private static ArrayList<Node> fs;

	public static void main(final String... args) throws FuseException
	{
		if (args.length != 1) {
			System.err.println("Usage: HelloFS <mountpoint>");
			System.exit(1);
		}
        initSandboxFs();
		new SandboxFS().log(true).mount(args[0]);
	}

    private static void initSandboxFs(){
        fs = new ArrayList<Node>();

        Node dirNode = new Node(Node.NODE_DIR, "/", "dir1", "");
        fs.add(dirNode);

        Node fileNode1 = new Node(Node.NODE_FILE, "/", "file1", "file 1 content\n");
        fs.add(fileNode1);

        Node fileNode2 = new Node(Node.NODE_FILE, "/dir1", "file2" , "file 2 content\n");
        fs.add(fileNode2);

    }





	@Override
	public int getattr(final String path, final StatWrapper stat)
	{
        String fullname = "";

        //also can do such things as
        //stat.uid(UID)/gid(GID) etc

		if (path.equals(File.separator)) { // Root directory
			stat.setMode(NodeType.DIRECTORY);
			return 0;
		}

        for (Node node : fs){

            if (node.getPath().equals("/")){
                fullname = node.getPath() + node.getName();
            } else {
                fullname = node.getPath() + File.separator + node.getName();
            }
            if (path.equals(fullname)){
                if (node.isDir()){
                    stat.setMode(NodeType.DIRECTORY);
                } else {
                    stat.setMode(NodeType.FILE).size(node.getSize());
                }
                return 0;
            }
        }

		return -ErrorCodes.ENOENT();
	}

	@Override
	public int read(final String path, final ByteBuffer buffer, final long size, final long offset, final FileInfoWrapper info)
	{
        //id = treeController.resolvePath(path);
        //content = fileContentController.getContentById(id);
        //return content;


        //treeController.resolvePath(path){
        /*

            if (insideCache(path)) return cache(path) else ASKDHT(path)
        }
         */



        String fullname = "";

        for (Node node : fs){

            if (node.getPath().equals("/")){
                fullname = node.getPath() + node.getName();
            } else {
                fullname = node.getPath() + File.separator + node.getName();
            }

            if (path.equals(fullname)){
                final String s = node.getContent().substring((int) offset,
                        (int) Math.max(offset, Math.min(node.getContent().length() - offset, offset + size)));
                buffer.put(s.getBytes());
                return s.getBytes().length;
            }
        }
        return 0;
	}

	@Override
	public int readdir(final String path, final DirectoryFiller filler)
	{
        for (Node node : fs){
            if (path.equals(node.getPath())){
                filler.add(node.getName());

            }
        }
		return 0;
	}
}
