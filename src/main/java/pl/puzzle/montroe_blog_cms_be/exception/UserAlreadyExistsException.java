package pl.puzzle.montroe_blog_cms_be.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("User already exists");
    }
}