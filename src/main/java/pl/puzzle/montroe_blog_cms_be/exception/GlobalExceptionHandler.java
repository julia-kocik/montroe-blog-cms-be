package pl.puzzle.montroe_blog_cms_be.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFound(
            NotFoundException exception
    ) {
        return ResponseEntity.notFound().build();
    }
}
