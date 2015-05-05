package challengetask.group02.controllers;

public class BusyException extends Throwable {
    public BusyException(String message) {
        super(message);
    }
}