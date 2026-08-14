package pl.puzzle.montroe_blog_cms_be.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Article not found");
    }

    public NotFoundException(String message) {
        super(message);
    }
}