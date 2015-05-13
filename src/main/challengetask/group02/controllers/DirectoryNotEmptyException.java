package challengetask.group02.controllers;

public class DirectoryNotEmptyException extends FsException {
    public DirectoryNotEmptyException(String path) {
        super(path);
    }
}
