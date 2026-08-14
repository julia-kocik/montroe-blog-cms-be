package pl.puzzle.montroe_blog_cms_be.article_summary_item;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.puzzle.montroe_blog_cms_be.article.Article;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemUpdateRequest;

import java.util.UUID;


@Entity
@Table(name = "article_summary_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ArticleSummaryItem {
    @Id
    private UUID id;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    private Article article;

    public static ArticleSummaryItem create(
            ArticleSummaryItemCreateRequest request,
            Article article,
            int position
    ) {
        return ArticleSummaryItem.builder()
                .id(UUID.randomUUID())
                .position(position)
                .name(request.name())
                .article(article)
                .build();
    }

    public void update(
            ArticleSummaryItemUpdateRequest request,
            int position
    ) {
        if (request.name() != null) {
            this.name = request.name();
        }

        this.position = position;
    }
}