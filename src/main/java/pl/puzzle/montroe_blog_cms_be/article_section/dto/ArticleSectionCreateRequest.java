package pl.puzzle.montroe_blog_cms_be.article_section.dto;

public record ArticleSectionCreateRequest(
        String subHeading,
        String paragraph,
        String imageLarge,
        String imageSmall
) {
}