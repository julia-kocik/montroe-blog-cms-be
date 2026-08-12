package pl.puzzle.montroe_blog_cms_be.article.dto;

import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemUpdateRequest;

import java.util.List;

public record ArticleUpdateRequest(
        String name,
        String lead,
        String image,
        List<ArticleSummaryItemUpdateRequest> summaryItems,
        List<ArticleSectionUpdateRequest> sections,
        List<ArticleTableOfContentItemUpdateRequest> tableOfContentItems
) {
}