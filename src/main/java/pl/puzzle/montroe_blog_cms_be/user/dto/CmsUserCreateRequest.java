package pl.puzzle.montroe_blog_cms_be.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CmsUserCreateRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String password
) {
}