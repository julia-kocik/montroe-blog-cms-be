package pl.puzzle.montroe_blog_cms_be.article;

import org.springframework.stereotype.Service;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleListItemResponse;

import java.util.List;

@Service
public class ArticleGetAllService {

    private final ArticleRepository articleRepository;

    public ArticleGetAllService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<ArticleListItemResponse> getAllArticles() {
        return articleRepository.findAll()
                .stream()
                .map(article -> new ArticleListItemResponse(
                        article.getId(),
                        article.getPublicationDate(),
                        article.getName(),
                        article.getImage(),
                        article.getLead(),
                        article.getPath()
                ))
                .toList();
    }
}