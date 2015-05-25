package challengetask.group02.controllers.exceptions;

public class NoSuchFileOrDirectoryException extends FsException {
    public NoSuchFileOrDirectoryException(String s) {
        super(s);
    }
}
