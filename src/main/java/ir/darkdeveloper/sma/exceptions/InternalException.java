package ir.darkdeveloper.sma.exceptions;

public class InternalException extends RuntimeException{
    public InternalException() {
    }

    public InternalException(String message) {
        super(message);
    }

    public InternalException(Throwable cause) {
        super(cause);
    }
}
