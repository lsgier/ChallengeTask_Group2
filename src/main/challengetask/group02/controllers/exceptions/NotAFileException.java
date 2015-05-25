package challengetask.group02.controllers.exceptions;

public class NotAFileException extends FsException {
    public NotAFileException(String message) {
        super(message);
    }
}
