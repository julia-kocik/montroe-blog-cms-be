package pl.puzzle.montroe_blog_cms_be.user.dto;

import pl.puzzle.montroe_blog_cms_be.user.CmsUser;

import java.util.UUID;

public record CmsUserResponse(
        UUID id,
        String email,
        String role,
        boolean enabled
) {

    public static CmsUserResponse from(CmsUser user) {
        return new CmsUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.isEnabled()
        );
    }
}