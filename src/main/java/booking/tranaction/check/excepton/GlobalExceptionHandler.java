package booking.tranaction.check.excepton;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<String> handleServerWebInputException(ServerWebInputException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(UnsavedCreditLimitException.class)
    public Mono<ResponseEntity<String>> handleUnsavedCreditLimitException(ServerWebInputException ex) {
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public Mono<ResponseEntity<String>> handleNotFoundException(ChangeSetPersister.NotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found"));
    }

    @ExceptionHandler(NullPointerException.class)
    public Mono<ResponseEntity<String>> handleNullPointerException(NullPointerException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }
    @ExceptionHandler(CustomNullPointerException.class)
    public Mono<ResponseEntity<String>> handleNullPointerException(CustomNullPointerException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }
}
