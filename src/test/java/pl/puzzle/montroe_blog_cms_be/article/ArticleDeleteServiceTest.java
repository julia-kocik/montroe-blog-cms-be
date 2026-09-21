package pl.puzzle.montroe_blog_cms_be.article;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puzzle.montroe_blog_cms_be.exception.NotFoundException;
import pl.puzzle.montroe_blog_cms_be.file.FileStorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleDeleteServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ArticleDeleteService articleDeleteService;

    @Test
    void shouldDeleteArticle() {
        UUID articleId = UUID.randomUUID();

        Article article = Article.builder()
                .id(articleId)
                .image("articles/main.jpg")
                .sections(List.of())
                .build();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.of(article));

        articleDeleteService.deleteArticle(articleId);

        verify(articleRepository).delete(article);
        verify(articleRepository).flush();
        verify(fileStorageService)
                .deleteImage("articles/main.jpg");
    }

    @Test
    void shouldThrowExceptionWhenArticleDoesNotExist() {
        UUID articleId = UUID.randomUUID();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                articleDeleteService.deleteArticle(articleId)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Article not found");
    }
}