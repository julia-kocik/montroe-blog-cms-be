package pl.puzzle.montroe_blog_cms_be.exception;

public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }
}