package pl.puzzle.montroe_blog_cms_be.article;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_section.ArticleSection;
import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.ArticleSummaryItem;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.ArticleTableOfContentItem;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleUpdateServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleUpdateService articleUpdateService;

    @Test
    void shouldUpdateOnlyArticleName() {
        Article article = Article.createArticle(
                new ArticleCreateRequest(
                        "Old name",
                        "Old lead",
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        UUID articleId = article.getId();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.of(article));

        ArticleUpdateRequest request = new ArticleUpdateRequest(
                "New name",
                null,
                null,
                null,
                null,
                null
        );

        Article result = articleUpdateService.updateArticle(
                articleId,
                request
        );

        assertThat(result.getName())
                .isEqualTo("New name");

        assertThat(result.getLead())
                .isEqualTo("Old lead");

        assertThat(result.getPath())
                .isEqualTo("new-name");
    }

    @Test
    void shouldUpdateExistingSummaryItem() {
        Article article = Article.createArticle(
                new ArticleCreateRequest(
                        "Article",
                        "Lead",
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        ArticleSummaryItem summaryItem = ArticleSummaryItem.create(
                new ArticleSummaryItemCreateRequest("Old summary"),
                article,
                1
        );

        article.addSummaryItem(summaryItem);

        UUID articleId = article.getId();
        UUID summaryItemId = summaryItem.getId();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.of(article));

        ArticleUpdateRequest request = new ArticleUpdateRequest(
                null,
                null,
                null,
                List.of(
                        new ArticleSummaryItemUpdateRequest(
                                summaryItemId,
                                "New summary"
                        )
                ),
                null,
                null
        );

        Article result = articleUpdateService.updateArticle(
                articleId,
                request
        );

        assertThat(result.getSummaryItems())
                .hasSize(1);

        assertThat(result.getSummaryItems().getFirst().getId())
                .isEqualTo(summaryItemId);

        assertThat(result.getSummaryItems().getFirst().getName())
                .isEqualTo("New summary");

        assertThat(result.getSummaryItems().getFirst().getPosition())
                .isEqualTo(1);
    }

    @Test
    void shouldAddNewSummaryItem() {
        Article article = Article.createArticle(
                new ArticleCreateRequest(
                        "Article",
                        "Lead",
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        UUID articleId = article.getId();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.of(article));

        ArticleUpdateRequest request = new ArticleUpdateRequest(
                null,
                null,
                null,
                List.of(
                        new ArticleSummaryItemUpdateRequest(
                                null,
                                "New summary"
                        )
                ),
                null,
                null
        );

        Article result = articleUpdateService.updateArticle(
                articleId,
                request
        );

        assertThat(result.getSummaryItems())
                .hasSize(1);

        ArticleSummaryItem newItem =
                result.getSummaryItems().getFirst();

        assertThat(newItem.getId())
                .isNotNull();

        assertThat(newItem.getName())
                .isEqualTo("New summary");

        assertThat(newItem.getPosition())
                .isEqualTo(1);

        assertThat(newItem.getArticle())
                .isEqualTo(article);
    }

    @Test
    void shouldRemoveSummaryItemNotIncludedInRequest() {
        Article article = Article.createArticle(
                new ArticleCreateRequest(
                        "Article",
                        "Lead",
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        ArticleSummaryItem firstItem = ArticleSummaryItem.create(
                new ArticleSummaryItemCreateRequest("First summary"),
                article,
                1
        );

        ArticleSummaryItem secondItem = ArticleSummaryItem.create(
                new ArticleSummaryItemCreateRequest("Second summary"),
                article,
                2
        );

        article.addSummaryItem(firstItem);
        article.addSummaryItem(secondItem);

        UUID articleId = article.getId();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.of(article));

        ArticleUpdateRequest request = new ArticleUpdateRequest(
                null,
                null,
                null,
                List.of(
                        new ArticleSummaryItemUpdateRequest(
                                secondItem.getId(),
                                "Second summary"
                        )
                ),
                null,
                null
        );

        Article result = articleUpdateService.updateArticle(
                articleId,
                request
        );

        assertThat(result.getSummaryItems())
                .hasSize(1);

        assertThat(result.getSummaryItems().getFirst().getId())
                .isEqualTo(secondItem.getId());

        assertThat(result.getSummaryItems().getFirst().getPosition())
                .isEqualTo(1);
    }


    @Test
    void shouldRemoveAllSummaryItemsWhenEmptyListIsProvided() {
        Article article = Article.createArticle(
                new ArticleCreateRequest(
                        "Article",
                        "Lead",
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        ArticleSummaryItem summaryItem = ArticleSummaryItem.create(
                new ArticleSummaryItemCreateRequest("Summary"),
                article,
                1
        );

        article.addSummaryItem(summaryItem);

        UUID articleId = article.getId();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.of(article));

        ArticleUpdateRequest request = new ArticleUpdateRequest(
                null,
                null,
                null,
                List.of(),
                null,
                null
        );

        Article result = articleUpdateService.updateArticle(
                articleId,
                request
        );

        assertThat(result.getSummaryItems())
                .isEmpty();
    }

    @Test
    void shouldUpdateExistingSectionAndRegenerateSlug() {
        Article article = Article.createArticle(
                new ArticleCreateRequest(
                        "Article",
                        "Lead",
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        ArticleSection section = ArticleSection.create(
                new ArticleSectionCreateRequest(
                        "Old heading",
                        "Old paragraph",
                        "old-large.jpg",
                        "old-small.jpg"
                ),
                article,
                1
        );

        article.addSection(section);

        UUID articleId = article.getId();
        UUID sectionId = section.getId();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.of(article));

        ArticleUpdateRequest request = new ArticleUpdateRequest(
                null,
                null,
                null,
                null,
                List.of(
                        new ArticleSectionUpdateRequest(
                                sectionId,
                                "Nowy nagłówek sekcji",
                                "New paragraph",
                                "new-large.jpg",
                                "new-small.jpg"
                        )
                ),
                null
        );

        Article result = articleUpdateService.updateArticle(
                articleId,
                request
        );

        assertThat(result.getSections())
                .hasSize(1);

        ArticleSection updatedSection =
                result.getSections().getFirst();

        assertThat(updatedSection.getId())
                .isEqualTo(sectionId);

        assertThat(updatedSection.getSubHeading())
                .isEqualTo("Nowy nagłówek sekcji");

        assertThat(updatedSection.getParagraph())
                .isEqualTo("New paragraph");

        assertThat(updatedSection.getImageLarge())
                .isEqualTo("new-large.jpg");

        assertThat(updatedSection.getImageSmall())
                .isEqualTo("new-small.jpg");

        assertThat(updatedSection.getSlug())
                .isEqualTo("nowy-naglowek-sekcji");

        assertThat(updatedSection.getPosition())
                .isEqualTo(1);
    }

    @Test
    void shouldUpdateExistingTableOfContentItemAndRegenerateLink() {
        Article article = Article.createArticle(
                new ArticleCreateRequest(
                        "Article",
                        "Lead",
                        List.of(),
                        List.of(),
                        List.of()
                )
        );

        ArticleTableOfContentItem item =
                ArticleTableOfContentItem.create(
                        new ArticleTableOfContentItemCreateRequest(
                                "Old heading"
                        ),
                        article,
                        1
                );

        article.addTableOfContentItem(item);

        UUID articleId = article.getId();
        UUID itemId = item.getId();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.of(article));

        ArticleUpdateRequest request = new ArticleUpdateRequest(
                null,
                null,
                null,
                null,
                null,
                List.of(
                        new ArticleTableOfContentItemUpdateRequest(
                                itemId,
                                "Nowy nagłówek"
                        )
                )
        );

        Article result = articleUpdateService.updateArticle(
                articleId,
                request
        );

        assertThat(result.getTableOfContentItems())
                .hasSize(1);

        ArticleTableOfContentItem updatedItem =
                result.getTableOfContentItems().getFirst();

        assertThat(updatedItem.getId())
                .isEqualTo(itemId);

        assertThat(updatedItem.getName())
                .isEqualTo("Nowy nagłówek");

        assertThat(updatedItem.getLink())
                .isEqualTo("#nowy-naglowek");

        assertThat(updatedItem.getPosition())
                .isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWhenArticleDoesNotExist() {
        UUID articleId = UUID.randomUUID();

        when(articleRepository.findById(articleId))
                .thenReturn(Optional.empty());

        ArticleUpdateRequest request = new ArticleUpdateRequest(
                "New name",
                null,
                null,
                null,
                null,
                null
        );

        assertThatThrownBy(() ->
                articleUpdateService.updateArticle(articleId, request)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Article not found");
    }
}