package pl.puzzle.montroe_blog_cms_be.article;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.ArticleSummaryItem;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemCreateRequest;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ArticleIntegrationTest {

    @Autowired
    private ArticleCreateService articleCreateService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleDeleteService articleDeleteService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ArticleUpdateService articleUpdateService;

    @Test
    void shouldCreateArticleWithAllRelatedEntities() {
        ArticleCreateRequest request = new ArticleCreateRequest(
                "Integration test article",
                "Integration test lead",
                List.of(
                        new ArticleSummaryItemCreateRequest(
                                "First summary"
                        ),
                        new ArticleSummaryItemCreateRequest(
                                "Second summary"
                        )
                ),
                List.of(
                        new ArticleSectionCreateRequest(
                                "First section",
                                "First paragraph",
                                "",
                                ""
                        ),
                        new ArticleSectionCreateRequest(
                                "Second section",
                                "Second paragraph",
                                "",
                                ""
                        )
                ),
                List.of(
                        new ArticleTableOfContentItemCreateRequest(
                                "First section"
                        ),
                        new ArticleTableOfContentItemCreateRequest(
                                "Second section"
                        )
                )
        );

        Article created =
                articleCreateService.createArticle(request);

        Article saved = articleRepository.findById(created.getId())
                .orElseThrow();

        assertThat(saved.getName())
                .isEqualTo("Integration test article");

        assertThat(saved.getSummaryItems())
                .hasSize(2);

        assertThat(saved.getSections())
                .hasSize(2);

        assertThat(saved.getTableOfContentItems())
                .hasSize(2);

        assertThat(saved.getSummaryItems().get(0).getArticle().getId())
                .isEqualTo(saved.getId());

        assertThat(saved.getSections().get(0).getArticle().getId())
                .isEqualTo(saved.getId());

        assertThat(saved.getTableOfContentItems().get(0).getArticle().getId())
                .isEqualTo(saved.getId());
    }

    @Test
    void shouldDeleteArticleWithAllRelatedEntities() {
        ArticleCreateRequest request = new ArticleCreateRequest(
                "Article to delete",
                "Lead",
                List.of(
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

        Article created =
                articleCreateService.createArticle(request);

        UUID articleId = created.getId();

        entityManager.flush();
        entityManager.clear();

        articleDeleteService.deleteArticle(articleId);

        entityManager.flush();
        entityManager.clear();

        assertThat(articleRepository.findById(articleId))
                .isEmpty();

        Long summaryItemsCount = entityManager.createQuery(
                        """
                        SELECT COUNT(s)
                        FROM ArticleSummaryItem s
                        WHERE s.article.id = :articleId
                        """,
                        Long.class
                )
                .setParameter("articleId", articleId)
                .getSingleResult();

        Long sectionsCount = entityManager.createQuery(
                        """
                        SELECT COUNT(s)
                        FROM ArticleSection s
                        WHERE s.article.id = :articleId
                        """,
                        Long.class
                )
                .setParameter("articleId", articleId)
                .getSingleResult();

        Long tableOfContentItemsCount = entityManager.createQuery(
                        """
                        SELECT COUNT(t)
                        FROM ArticleTableOfContentItem t
                        WHERE t.article.id = :articleId
                        """,
                        Long.class
                )
                .setParameter("articleId", articleId)
                .getSingleResult();

        assertThat(summaryItemsCount).isZero();
        assertThat(sectionsCount).isZero();
        assertThat(tableOfContentItemsCount).isZero();
    }

    @Test
    void shouldUpdateArticleAndRemoveOrphanedSummaryItem() {
        Article created = articleCreateService.createArticle(
                new ArticleCreateRequest(
                        "Original article",
                        "Original lead",
                        List.of(
                                new ArticleSummaryItemCreateRequest(
                                        "First summary"
                                ),
                                new ArticleSummaryItemCreateRequest(
                                        "Second summary"
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
                )
        );

        UUID articleId = created.getId();

        UUID firstSummaryId =
                created.getSummaryItems().get(0).getId();

        UUID secondSummaryId =
                created.getSummaryItems().get(1).getId();

        entityManager.flush();
        entityManager.clear();

        ArticleUpdateRequest updateRequest =
                new ArticleUpdateRequest(
                        "Updated article",
                        null,
                        null,
                        List.of(
                                new ArticleSummaryItemUpdateRequest(
                                        secondSummaryId,
                                        "Updated second summary"
                                )
                        ),
                        null,
                        null
                );

        articleUpdateService.updateArticle(
                articleId,
                updateRequest
        );

        entityManager.flush();
        entityManager.clear();

        Article updated = articleRepository.findById(articleId)
                .orElseThrow();

        assertThat(updated.getName())
                .isEqualTo("Updated article");

        assertThat(updated.getLead())
                .isEqualTo("Original lead");

        assertThat(updated.getSummaryItems())
                .hasSize(1);

        ArticleSummaryItem remainingItem =
                updated.getSummaryItems().getFirst();

        assertThat(remainingItem.getId())
                .isEqualTo(secondSummaryId);

        assertThat(remainingItem.getName())
                .isEqualTo("Updated second summary");

        assertThat(remainingItem.getPosition())
                .isEqualTo(1);

        Long removedItemCount = entityManager.createQuery(
                        """
                        SELECT COUNT(s)
                        FROM ArticleSummaryItem s
                        WHERE s.id = :summaryId
                        """,
                        Long.class
                )
                .setParameter("summaryId", firstSummaryId)
                .getSingleResult();

        assertThat(removedItemCount)
                .isZero();
    }
}