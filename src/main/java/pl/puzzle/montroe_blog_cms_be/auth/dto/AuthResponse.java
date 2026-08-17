package pl.puzzle.montroe_blog_cms_be.auth.dto;

public record AuthResponse(
        String email,
        String role
) {
}