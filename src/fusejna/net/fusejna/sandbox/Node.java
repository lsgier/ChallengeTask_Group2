package net.fusejna.sandbox;

/**
 * Created by anvar on 09/04/15.
 */
public class Node {

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
