package pl.puzzle.montroe_blog_cms_be.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleListItemResponse;
import pl.puzzle.montroe_blog_cms_be.article.dto.PageResponse;

import java.util.List;

@Service
public class ArticleGetAllService {

    private final ArticleRepository articleRepository;

    public ArticleGetAllService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<ArticleListItemResponse> getAllArticles() {
        return articleRepository.findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "publicationDate"
                        )
                )
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

    public PageResponse<ArticleListItemResponse> getArticlesPage(
            int page,
            int size
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "publicationDate"
                )
        );

        Page<ArticleListItemResponse> result = articleRepository
                .findAll(pageRequest)
                .map(article -> new ArticleListItemResponse(
                        article.getId(),
                        article.getPublicationDate(),
                        article.getName(),
                        article.getImage(),
                        article.getLead(),
                        article.getPath()
                ));

        return new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.isLast()
        );
    }
}