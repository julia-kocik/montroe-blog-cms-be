package pl.puzzle.montroe_blog_cms_be.article;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puzzle.montroe_blog_cms_be.exception.NotFoundException;

import java.util.UUID;

@Service
public class ArticleDeleteService {

    private final ArticleRepository articleRepository;

    public ArticleDeleteService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Transactional
    public void deleteArticle(UUID id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        articleRepository.delete(article);
    }

    @Transactional
    public void deleteAllArticles() {
        articleRepository.deleteAll();
    }
}