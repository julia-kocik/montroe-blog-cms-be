package pl.puzzle.montroe_blog_cms_be.article;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puzzle.montroe_blog_cms_be.exception.NotFoundException;
import pl.puzzle.montroe_blog_cms_be.file.FileStorageService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ArticleDeleteService {

    private final ArticleRepository articleRepository;
    private final FileStorageService fileStorageService;

    public ArticleDeleteService(
            ArticleRepository articleRepository,
            FileStorageService fileStorageService
    ) {
        this.articleRepository = articleRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public void deleteArticle(UUID id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        List<String> imageKeys = Stream.concat(
                        Stream.of(article.getImage()),
                        article.getSections()
                                .stream()
                                .flatMap(section ->
                                        Stream.of(
                                                section.getImageLarge(),
                                                section.getImageSmall()
                                        )
                                )
                )
                .filter(Objects::nonNull)
                .filter(key -> !key.isBlank())
                .distinct()
                .toList();

        articleRepository.delete(article);
        articleRepository.flush();

        imageKeys.forEach(
                fileStorageService::deleteImage
        );
    }
}