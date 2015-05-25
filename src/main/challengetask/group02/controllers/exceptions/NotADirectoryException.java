package challengetask.group02.controllers.exceptions;

public class NotADirectoryException extends FsException{
    public NotADirectoryException(String message) {
        super(message);
    }
}
