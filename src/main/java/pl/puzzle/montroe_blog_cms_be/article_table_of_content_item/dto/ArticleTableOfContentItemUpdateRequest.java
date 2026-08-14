package pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto;

import java.util.UUID;

public record ArticleTableOfContentItemUpdateRequest(
        UUID id,
        String name
) {
}