package pl.puzzle.montroe_blog_cms_be.article;

import org.springframework.stereotype.Service;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;



@Service
public class ArticleCreateService {
    private final ArticleRepository articleRepository;

    public ArticleCreateService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article createArticle(ArticleCreateRequest articleCreateRequest) {

        Article article = Article.createArticle(articleCreateRequest);
        articleRepository.save(article);

        return article;
    }
}
