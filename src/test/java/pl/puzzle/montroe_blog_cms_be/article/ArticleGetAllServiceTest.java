package pl.puzzle.montroe_blog_cms_be.article;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleListItemResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleGetAllServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleGetAllService articleGetAllService;

    @Test
    void shouldReturnAllArticlesAsListItems() {
        UUID articleId = UUID.randomUUID();
        LocalDateTime publicationDate = LocalDateTime.now();

        Article article = Article.builder()
                .id(articleId)
                .publicationDate(publicationDate)
                .name("Test article")
                .image("test-image")
                .lead("Test lead")
                .path("test-article")
                .build();

        when(articleRepository.findAll())
                .thenReturn(List.of(article));

        List<ArticleListItemResponse> result =
                articleGetAllService.getAllArticles();

        assertThat(result)
                .hasSize(1);

        ArticleListItemResponse item = result.getFirst();

        assertThat(item.id())
                .isEqualTo(articleId);

        assertThat(item.publicationDate())
                .isEqualTo(publicationDate);

        assertThat(item.name())
                .isEqualTo("Test article");

        assertThat(item.image())
                .isEqualTo("test-image");

        assertThat(item.lead())
                .isEqualTo("Test lead");

        assertThat(item.path())
                .isEqualTo("test-article");
    }
}