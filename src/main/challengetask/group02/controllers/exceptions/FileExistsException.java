package challengetask.group02.controllers.exceptions;

public class FileExistsException extends FsException {
    public FileExistsException(String m) {
        super(m);
    }
}
