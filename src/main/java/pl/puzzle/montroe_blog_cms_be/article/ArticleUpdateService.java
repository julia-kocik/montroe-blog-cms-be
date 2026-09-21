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
import pl.puzzle.montroe_blog_cms_be.exception.NotFoundException;
import pl.puzzle.montroe_blog_cms_be.file.FileStorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArticleUpdateService {

    private final ArticleRepository articleRepository;
    private final FileStorageService fileStorageService;

    public ArticleUpdateService(
            ArticleRepository articleRepository,
            FileStorageService fileStorageService
    ) {
        this.articleRepository = articleRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public Article updateArticle(
            UUID id,
            ArticleUpdateRequest request
    ) {
        Article article = articleRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        String oldImage = article.getImage();

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

        if (request.image() != null
                && !request.image().equals(oldImage)
                && isR2Image(oldImage)) {

            articleRepository.flush();

            fileStorageService.deleteImage(oldImage);
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
                                        new NotFoundException(
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
        List<String> imagesToDelete = new ArrayList<>();

        Set<UUID> requestIds = request.sections().stream()
                .map(ArticleSectionUpdateRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        article.getSections().stream()
                .filter(section ->
                        !requestIds.contains(section.getId())
                )
                .forEach(section -> {
                    if (isR2Image(section.getImageLarge())) {
                        imagesToDelete.add(
                                section.getImageLarge()
                        );
                    }

                    if (isR2Image(section.getImageSmall())) {
                        imagesToDelete.add(
                                section.getImageSmall()
                        );
                    }
                });

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
                                        new NotFoundException(
                                                "Section not found"
                                        )
                                );

                if (sectionRequest.imageLarge() != null
                        && !sectionRequest.imageLarge()
                        .equals(existingSection.getImageLarge())
                        && isR2Image(existingSection.getImageLarge())) {

                    imagesToDelete.add(
                            existingSection.getImageLarge()
                    );
                }

                if (sectionRequest.imageSmall() != null
                        && !sectionRequest.imageSmall()
                        .equals(existingSection.getImageSmall())
                        && isR2Image(existingSection.getImageSmall())) {

                    imagesToDelete.add(
                            existingSection.getImageSmall()
                    );
                }

                existingSection.update(
                        sectionRequest,
                        position
                );
            }
        }

        imagesToDelete.stream()
                .distinct()
                .forEach(fileStorageService::deleteImage);
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
                                        item.getId()
                                                .equals(itemRequest.id())
                                )
                                .findFirst()
                                .orElseThrow(() ->
                                        new NotFoundException(
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

    private boolean isR2Image(String key) {
        return key != null
                && !key.isBlank()
                && key.startsWith("articles/");
    }
}