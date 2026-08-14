package pl.puzzle.montroe_blog_cms_be.article.dto;


import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleListItemResponse(
        UUID id,
        LocalDateTime publicationDate,
        String name,
        String image,
        String lead,
        String path
) {
}
