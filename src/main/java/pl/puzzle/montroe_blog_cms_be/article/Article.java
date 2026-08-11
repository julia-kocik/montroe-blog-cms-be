package pl.puzzle.montroe_blog_cms_be.article;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;

import static lombok.AccessLevel.PRIVATE;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "article")
@NoArgsConstructor(access = PRIVATE, force = true)
@AllArgsConstructor(access = PRIVATE)
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

    public static Article createArticle(ArticleCreateRequest articleCreateRequest) {
        String path = toSlug(articleCreateRequest.name());
        LocalDateTime now = LocalDateTime.now();
        //TODO: image extension
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

    public static String toSlug(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

}
