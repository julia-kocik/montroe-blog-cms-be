package pl.puzzle.montroe_blog_cms_be.article_section;

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
import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionUpdateRequest;

import java.util.UUID;

@Entity
@Table(name = "article_section")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ArticleSection {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "sub_heading", nullable = false)
    private String subHeading;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String paragraph;

    @Column(name = "image_large")
    private String imageLarge;

    @Column(name = "image_small")
    private String imageSmall;

    @Column(nullable = false)
    private String slug;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    private Article article;

    public static ArticleSection create(
            ArticleSectionCreateRequest request,
            Article article,
            int position
    ) {
        return ArticleSection.builder()
                .id(UUID.randomUUID())
                .position(position)
                .subHeading(request.subHeading())
                .paragraph(request.paragraph())
                .imageLarge(request.imageLarge())
                .imageSmall(request.imageSmall())
                .slug(Article.toSlug(request.subHeading()))
                .article(article)
                .build();
    }

    public void update(
            ArticleSectionUpdateRequest request,
            int position
    ) {
        if (request.subHeading() != null) {
            this.subHeading = request.subHeading();
            this.slug = Article.toSlug(request.subHeading());
        }

        if (request.paragraph() != null) {
            this.paragraph = request.paragraph();
        }

        if (request.imageLarge() != null) {
            this.imageLarge = request.imageLarge();
        }

        if (request.imageSmall() != null) {
            this.imageSmall = request.imageSmall();
        }

        this.position = position;
    }
}