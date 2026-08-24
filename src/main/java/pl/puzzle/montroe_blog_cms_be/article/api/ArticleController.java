package pl.puzzle.montroe_blog_cms_be.article.api;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.puzzle.montroe_blog_cms_be.article.Article;
import pl.puzzle.montroe_blog_cms_be.article.ArticleCreateService;
import pl.puzzle.montroe_blog_cms_be.article.ArticleDeleteService;
import pl.puzzle.montroe_blog_cms_be.article.ArticleGetAllService;
import pl.puzzle.montroe_blog_cms_be.article.ArticleGetService;
import pl.puzzle.montroe_blog_cms_be.article.ArticleUpdateService;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleListItemResponse;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleUpdateRequest;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleCreateService articleCreateService;
    private final ArticleUpdateService articleUpdateService;
    private final ArticleGetService articleGetService;
    private final ArticleGetAllService articleGetAllService;
    private final ArticleDeleteService articleDeleteService;

    public ArticleController(ArticleCreateService articleCreateService, ArticleUpdateService articleUpdateService, ArticleGetService articleGetService, ArticleGetAllService articleGetAllService, ArticleDeleteService articleDeleteService) {
        this.articleCreateService = articleCreateService;
        this.articleUpdateService = articleUpdateService;
        this.articleGetService = articleGetService;
        this.articleGetAllService = articleGetAllService;
        this.articleDeleteService = articleDeleteService;
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(
            @Valid @RequestBody ArticleCreateRequest request
    ) {
        Article article = articleCreateService.createArticle(request);

        return ResponseEntity.ok(article);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable UUID id,
            @RequestBody ArticleUpdateRequest request
    ) {
        return ResponseEntity.ok(
                articleUpdateService.updateArticle(id, request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                articleGetService.getArticle(id)
        );
    }

    @GetMapping("/path/{path}")
    public ResponseEntity<Article> getArticleByPath(
            @PathVariable String path
    ) {
        return ResponseEntity.ok(
                articleGetService.getArticleByPath(path)
        );
    }

    @GetMapping
    public ResponseEntity<List<ArticleListItemResponse>> getAllArticles() {
        return ResponseEntity.ok(
                articleGetAllService.getAllArticles()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable UUID id
    ) {
        articleDeleteService.deleteArticle(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllArticles() {
        articleDeleteService.deleteAllArticles();

        return ResponseEntity.noContent().build();
    }
}