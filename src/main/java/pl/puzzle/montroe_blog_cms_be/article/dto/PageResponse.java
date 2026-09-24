package pl.puzzle.montroe_blog_cms_be.article.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        int totalPages,
        long totalElements,
        boolean last
) {
}