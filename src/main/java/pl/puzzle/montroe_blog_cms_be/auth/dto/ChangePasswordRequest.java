package pl.puzzle.montroe_blog_cms_be.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,

        @NotBlank
        String newPassword
) {
}