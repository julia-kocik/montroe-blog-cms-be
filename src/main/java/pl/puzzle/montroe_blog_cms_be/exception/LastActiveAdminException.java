package pl.puzzle.montroe_blog_cms_be.exception;

public class LastActiveAdminException extends RuntimeException {

    public LastActiveAdminException() {
        super("Cannot disable the last active admin");
    }
}