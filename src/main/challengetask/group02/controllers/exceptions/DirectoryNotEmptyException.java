package challengetask.group02.controllers.exceptions;

public class DirectoryNotEmptyException extends FsException {
    public DirectoryNotEmptyException(String path) {
        super(path);
    }
}
