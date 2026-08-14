package pl.puzzle.montroe_blog_cms_be.article;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_section.ArticleSection;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.ArticleSummaryItem;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.ArticleTableOfContentItem;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "article")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Article {

    @Id
    private UUID id;

    private LocalDateTime publicationDate;

    private String name;

    private String image;

    private String path;

    private String lead;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "article",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ArticleSummaryItem> summaryItems = new ArrayList<>();

    @OneToMany(
            mappedBy = "article",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ArticleSection> sections = new ArrayList<>();

    @OneToMany(
            mappedBy = "article",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ArticleTableOfContentItem> tableOfContentItems = new ArrayList<>();

    public static Article createArticle(
            ArticleCreateRequest articleCreateRequest
    ) {
        String path = toSlug(articleCreateRequest.name());
        LocalDateTime now = LocalDateTime.now();

        // TODO: image extension
        return Article.builder()
                .id(UUID.randomUUID())
                .name(articleCreateRequest.name())
                .image(path)
                .path(path)
                .lead(articleCreateRequest.lead())
                .publicationDate(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void update(ArticleUpdateRequest request) {
        if (request.name() != null) {
            this.name = request.name();
            this.path = toSlug(request.name());
        }

        if (request.lead() != null) {
            this.lead = request.lead();
        }

        if (request.image() != null) {
            this.image = request.image();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public static String toSlug(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("ł", "l")
                .replace("Ł", "L")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    public void addSummaryItem(ArticleSummaryItem summaryItem) {
        summaryItems.add(summaryItem);
    }

    public void addSection(ArticleSection section) {
        sections.add(section);
    }

    public void addTableOfContentItem(ArticleTableOfContentItem item) {
        tableOfContentItems.add(item);
    }
}