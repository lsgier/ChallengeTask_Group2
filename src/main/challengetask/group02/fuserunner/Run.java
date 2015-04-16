package challengetask.group02.fuserunner;

import net.fusejna.*;
import net.fusejna.types.TypeMode;
import net.fusejna.util.FuseFilesystemAdapterFull;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by anvar on 15/04/15.
 */
public class Run extends FuseFilesystemAdapterFull {

    private static ArrayList<Node> fs;

    public static void main(final String... args) throws FuseException
    {
        if (args.length != 1) {
            System.err.println("Usage: HelloFS <mountpoint>");
            System.exit(1);
        }
        initSandboxFs();
        new Run().log(true).mount(args[0]);
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
    public int getattr(final String path, final StructStat.StatWrapper stat)
    {
        String fullname = "";

        //also can do such things as
        //stat.uid(UID)/gid(GID) etc

        if (path.equals(File.separator)) { // Root directory
            stat.setMode(TypeMode.NodeType.DIRECTORY);
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
                    stat.setMode(TypeMode.NodeType.DIRECTORY);
                } else {
                    stat.setMode(TypeMode.NodeType.FILE).size(node.getSize());
                }
                return 0;
            }
        }

        return -ErrorCodes.ENOENT();
    }

    @Override
    public int read(final String path, final ByteBuffer buffer, final long size, final long offset, final StructFuseFileInfo.FileInfoWrapper info)
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


    public static class Node {

        public static final int NODE_DIR = 0;
        public static final int NODE_FILE = 1;

        private int type;
        private int size;
        private String path;
        private String name;
        private String content;

        public Node(int type, String path, String name, String content) {
            this.type = type;
            this.name = name;
            this.path = path;
            this.size = content.length();
            this.content = content;
        }

        public boolean isDir(){
            if (this.type == NODE_DIR) return true;
            else return false;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
