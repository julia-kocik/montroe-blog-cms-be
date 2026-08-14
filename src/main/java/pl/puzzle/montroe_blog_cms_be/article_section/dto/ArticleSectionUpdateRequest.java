package pl.puzzle.montroe_blog_cms_be.article_section.dto;

import java.util.UUID;

public record ArticleSectionUpdateRequest(
        UUID id,
        String subHeading,
        String paragraph,
        String imageLarge,
        String imageSmall
) {
}