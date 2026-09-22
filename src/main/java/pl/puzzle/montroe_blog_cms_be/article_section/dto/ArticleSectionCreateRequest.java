package pl.puzzle.montroe_blog_cms_be.article_section.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.puzzle.montroe_blog_cms_be.article_section.MobileImageMode;

public record ArticleSectionCreateRequest(

        @NotBlank
        String subHeading,

        @NotBlank
        String paragraph,

        String imageLarge,

        String imageSmall,

        @NotNull
        MobileImageMode mobileImageMode

) {
}