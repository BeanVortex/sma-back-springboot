package ir.darkdeveloper.sma.exceptions;

public class PasswordException extends RuntimeException{

    public PasswordException() {
    }

    public PasswordException(String message) {
        super(message);
    }

    public PasswordException(Throwable cause) {
        super(cause);
    }
}
