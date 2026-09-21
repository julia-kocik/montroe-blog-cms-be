package pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto;

import jakarta.validation.constraints.NotBlank;

public record ArticleTableOfContentItemCreateRequest(

        @NotBlank
        String name

) {
}