package pl.puzzle.montroe_blog_cms_be.article;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemCreateRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleCreateServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleCreateService articleCreateService;

    @Test
    void shouldCreateArticle() {
        ArticleCreateRequest request = new ArticleCreateRequest(
                "Test article",
                "Test lead",
                List.of(
                        new ArticleSummaryItemCreateRequest(
                                "Summary"
                        ),
                        new ArticleSummaryItemCreateRequest(
                                "Summary"
                        )
                ),
                List.of(
                        new ArticleSectionCreateRequest(
                                "Section",
                                "Paragraph",
                                "",
                                ""
                        )
                ),
                List.of(
                        new ArticleTableOfContentItemCreateRequest(
                                "Section"
                        )
                )
        );

        when(articleRepository.save(any(Article.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Article result =
                articleCreateService.createArticle(request);

        assertThat(result.getName())
                .isEqualTo("Test article");

        assertThat(result.getLead())
                .isEqualTo("Test lead");

        assertThat(result.getSummaryItems())
                .hasSize(2);

        assertThat(result.getSections())
                .hasSize(1);

        assertThat(result.getTableOfContentItems())
                .hasSize(1);

        verify(articleRepository).save(any(Article.class));
    }

    @Test
    void shouldGenerateArticleStructureData() {
        ArticleCreateRequest request = new ArticleCreateRequest(
                "Montaż instalacji elektrycznej Warszawa",
                "Test lead",
                List.of(
                        new ArticleSummaryItemCreateRequest(
                                "Pierwszy punkt"
                        )
                ),
                List.of(
                        new ArticleSectionCreateRequest(
                                "Pierwsza sekcja",
                                "Treść sekcji",
                                "",
                                ""
                        )
                ),
                List.of(
                        new ArticleTableOfContentItemCreateRequest(
                                "Pierwsza sekcja"
                        )
                )
        );

        when(articleRepository.save(any(Article.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Article result =
                articleCreateService.createArticle(request);

        assertThat(result.getPath())
                .isEqualTo("montaz-instalacji-elektrycznej-warszawa");

        assertThat(result.getSummaryItems().getFirst().getPosition())
                .isEqualTo(1);

        assertThat(result.getSections().getFirst().getPosition())
                .isEqualTo(1);

        assertThat(result.getSections().getFirst().getSlug())
                .isEqualTo("pierwsza-sekcja");

        assertThat(result.getTableOfContentItems().getFirst().getPosition())
                .isEqualTo(1);

        assertThat(result.getTableOfContentItems().getFirst().getLink())
                .isEqualTo("#pierwsza-sekcja");
    }
}