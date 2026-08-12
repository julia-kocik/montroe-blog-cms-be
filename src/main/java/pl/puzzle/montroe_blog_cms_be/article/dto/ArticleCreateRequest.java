package pl.puzzle.montroe_blog_cms_be.article.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemCreateRequest;

import java.util.List;

public record ArticleCreateRequest(

        @NotBlank
        String name,

        @NotBlank
        String lead,

        @NotEmpty
        List<@Valid ArticleSummaryItemCreateRequest> summaryItems,

        @NotEmpty
        List<@Valid ArticleSectionCreateRequest> sections,

        @NotEmpty
        List<@Valid ArticleTableOfContentItemCreateRequest> tableOfContentItems

) {
}