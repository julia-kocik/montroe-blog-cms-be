package pl.puzzle.montroe_blog_cms_be.article;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleGetServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleGetService articleGetService;

    @Test
    void shouldReturnArticle() {
        UUID articleId = UUID.randomUUID();

        Article article = Article.builder()
                .id(articleId)
                .name("Test article")
                .build();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.of(article));

        Article result = articleGetService.getArticle(articleId);

        assertThat(result.getId())
                .isEqualTo(articleId);

        assertThat(result.getName())
                .isEqualTo("Test article");
    }

    @Test
    void shouldThrowExceptionWhenArticleDoesNotExist() {
        UUID articleId = UUID.randomUUID();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                articleGetService.getArticle(articleId)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Article not found");
    }
}