package pl.puzzle.montroe_blog_cms_be.article_section.dto;

import jakarta.validation.constraints.NotBlank;

public record ArticleSectionCreateRequest(

        @NotBlank
        String subHeading,

        @NotBlank
        String paragraph,

        String imageLarge,

        String imageSmall

) {
}