package pl.puzzle.montroe_blog_cms_be.article.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemUpdateRequest;

import java.util.List;

public record ArticleUpdateRequest(

        @NotBlank
        String name,

        @NotBlank
        String lead,

        String image,

        @NotEmpty
        List<@Valid ArticleSummaryItemUpdateRequest> summaryItems,

        @NotEmpty
        List<@Valid ArticleSectionUpdateRequest> sections,

        @NotEmpty
        List<@Valid ArticleTableOfContentItemUpdateRequest> tableOfContentItems

) {
}