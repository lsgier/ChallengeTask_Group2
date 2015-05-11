package challengetask.group02.controllers;

public class NotADirectoryException extends FsException{
    public NotADirectoryException(String message) {
        super(message);
    }
}
