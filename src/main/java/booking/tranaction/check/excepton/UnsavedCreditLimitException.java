package booking.tranaction.check.excepton;

public class UnsavedCreditLimitException extends RuntimeException {
    public UnsavedCreditLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsavedCreditLimitException(String message) {
        super(message);
    }


}
