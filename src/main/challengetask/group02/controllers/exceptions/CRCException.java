package challengetask.group02.controllers.exceptions;

public class CRCException extends Exception {
	public CRCException(String file) {
        super(file);
    }
}
