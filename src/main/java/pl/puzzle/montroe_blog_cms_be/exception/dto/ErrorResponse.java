package pl.puzzle.montroe_blog_cms_be.exception.dto;

public record ErrorResponse(
        int status,
        String message
) {
}