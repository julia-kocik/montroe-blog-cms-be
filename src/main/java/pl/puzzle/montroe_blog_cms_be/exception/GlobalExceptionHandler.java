package pl.puzzle.montroe_blog_cms_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.puzzle.montroe_blog_cms_be.exception.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFound(
            NotFoundException exception
    ) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Void> handleInvalidPassword(
            InvalidPasswordException exception
    ) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Void> handleUserAlreadyExists(
            UserAlreadyExistsException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(LastActiveAdminException.class)
    public ResponseEntity<ErrorResponse> handleLastActiveAdmin(
            LastActiveAdminException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                exception.getMessage()
                        )
                );
    }
}
