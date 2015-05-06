package challengetask.group02.controllers;

public class DirectoryNotEmptyException extends Throwable {
    public DirectoryNotEmptyException(String path) {
        super(path);
    }
}
