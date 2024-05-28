package exception;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message, Throwable e) {
        super(message, e);
    }
}
