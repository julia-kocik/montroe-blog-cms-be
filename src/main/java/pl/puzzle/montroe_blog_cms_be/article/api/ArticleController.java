package pl.puzzle.montroe_blog_cms_be.article.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.puzzle.montroe_blog_cms_be.article.Article;
import pl.puzzle.montroe_blog_cms_be.article.ArticleCreateService;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;

@RestController
@RequestMapping("/article")
public class ArticleController {
    private final ArticleCreateService articleCreateService;

    public ArticleController(ArticleCreateService articleCreateService) {
        this.articleCreateService = articleCreateService;
    }

    @PostMapping()
    public ResponseEntity<Article> createArticle(@RequestBody ArticleCreateRequest articleCreateRequest) {
        return ResponseEntity.ok(articleCreateService.createArticle(articleCreateRequest));
    }
}
