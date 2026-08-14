package pl.puzzle.montroe_blog_cms_be.article;

import org.springframework.stereotype.Service;
import pl.puzzle.montroe_blog_cms_be.exception.NotFoundException;

import java.util.UUID;

@Service
public class ArticleGetService {

    private final ArticleRepository articleRepository;

    public ArticleGetService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article getArticle(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }
}