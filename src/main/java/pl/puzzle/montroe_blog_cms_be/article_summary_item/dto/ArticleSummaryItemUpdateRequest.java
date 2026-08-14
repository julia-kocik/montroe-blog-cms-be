package pl.puzzle.montroe_blog_cms_be.article_summary_item.dto;

import java.util.UUID;

public record ArticleSummaryItemUpdateRequest(
        UUID id,
        String name
) {
}