package pl.puzzle.montroe_blog_cms_be.article;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArticleUpdateService {

    private final ArticleRepository articleRepository;

    public ArticleUpdateService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Transactional
    public Article updateArticle(
            UUID id,
            ArticleUpdateRequest request
    ) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        article.update(request);

        if (request.summaryItems() != null) {
            updateSummaryItems(article, request);
        }

        if (request.sections() != null) {
            updateSections(article, request);
        }

        if (request.tableOfContentItems() != null) {
            updateTableOfContentItems(article, request);
        }

        return article;
    }

    private void updateSummaryItems(
            Article article,
            ArticleUpdateRequest request
    ) {
        Set<UUID> requestIds = request.summaryItems().stream()
                .map(ArticleSummaryItemUpdateRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        article.getSummaryItems().removeIf(
                item -> !requestIds.contains(item.getId())
        );

        for (int i = 0; i < request.summaryItems().size(); i++) {
            ArticleSummaryItemUpdateRequest itemRequest =
                    request.summaryItems().get(i);

            int position = i + 1;

            if (itemRequest.id() == null) {
                ArticleSummaryItem newItem =
                        ArticleSummaryItem.create(
                                new ArticleSummaryItemCreateRequest(
                                        itemRequest.name()
                                ),
                                article,
                                position
                        );

                article.addSummaryItem(newItem);
            } else {
                ArticleSummaryItem existingItem =
                        article.getSummaryItems().stream()
                                .filter(item ->
                                        item.getId().equals(itemRequest.id())
                                )
                                .findFirst()
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Summary item not found"
                                        )
                                );

                existingItem.update(
                        itemRequest,
                        position
                );
            }
        }
    }

    private void updateSections(
            Article article,
            ArticleUpdateRequest request
    ) {
        Set<UUID> requestIds = request.sections().stream()
                .map(ArticleSectionUpdateRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        article.getSections().removeIf(
                section -> !requestIds.contains(section.getId())
        );

        for (int i = 0; i < request.sections().size(); i++) {
            ArticleSectionUpdateRequest sectionRequest =
                    request.sections().get(i);

            int position = i + 1;

            if (sectionRequest.id() == null) {
                ArticleSection newSection =
                        ArticleSection.create(
                                new ArticleSectionCreateRequest(
                                        sectionRequest.subHeading(),
                                        sectionRequest.paragraph(),
                                        sectionRequest.imageLarge(),
                                        sectionRequest.imageSmall()
                                ),
                                article,
                                position
                        );

                article.addSection(newSection);
            } else {
                ArticleSection existingSection =
                        article.getSections().stream()
                                .filter(section ->
                                        section.getId()
                                                .equals(sectionRequest.id())
                                )
                                .findFirst()
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Section not found"
                                        )
                                );

                existingSection.update(
                        sectionRequest,
                        position
                );
            }
        }
    }

    private void updateTableOfContentItems(
            Article article,
            ArticleUpdateRequest request
    ) {
        Set<UUID> requestIds =
                request.tableOfContentItems().stream()
                        .map(ArticleTableOfContentItemUpdateRequest::id)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        article.getTableOfContentItems().removeIf(
                item -> !requestIds.contains(item.getId())
        );

        for (int i = 0; i < request.tableOfContentItems().size(); i++) {
            ArticleTableOfContentItemUpdateRequest itemRequest =
                    request.tableOfContentItems().get(i);

            int position = i + 1;

            if (itemRequest.id() == null) {
                ArticleTableOfContentItem newItem =
                        ArticleTableOfContentItem.create(
                                new ArticleTableOfContentItemCreateRequest(
                                        itemRequest.name()
                                ),
                                article,
                                position
                        );

                article.addTableOfContentItem(newItem);
            } else {
                ArticleTableOfContentItem existingItem =
                        article.getTableOfContentItems().stream()
                                .filter(item ->
                                        item.getId().equals(itemRequest.id())
                                )
                                .findFirst()
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Table of content item not found"
                                        )
                                );

                existingItem.update(
                        itemRequest,
                        position
                );
            }
        }
    }
}