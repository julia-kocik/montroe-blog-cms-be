package pl.puzzle.montroe_blog_cms_be.article_table_of_content_item;

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
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemUpdateRequest;

import java.util.UUID;

@Entity
@Table(name = "article_table_of_content_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ArticleTableOfContentItem {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String link;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    private Article article;

    public static ArticleTableOfContentItem create(
            ArticleTableOfContentItemCreateRequest request,
            Article article,
            int position
    ) {
        return ArticleTableOfContentItem.builder()
                .id(UUID.randomUUID())
                .position(position)
                .name(request.name())
                .link("#" + Article.toSlug(request.name()))
                .article(article)
                .build();
    }

    public void update(
            ArticleTableOfContentItemUpdateRequest request,
            int position
    ) {
        if (request.name() != null) {
            this.name = request.name();
            this.link = "#" + Article.toSlug(request.name());
        }

        this.position = position;
    }
}