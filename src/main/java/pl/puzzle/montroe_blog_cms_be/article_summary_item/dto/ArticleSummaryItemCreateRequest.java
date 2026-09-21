package pl.puzzle.montroe_blog_cms_be.article_summary_item.dto;

import jakarta.validation.constraints.NotBlank;

public record ArticleSummaryItemCreateRequest(

        @NotBlank
        String name

) {
}