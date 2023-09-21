package booking.tranaction.check.excepton;

public class CustomNullPointerException extends RuntimeException {
    public CustomNullPointerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomNullPointerException(String message) {
        super(message);
    }
}
