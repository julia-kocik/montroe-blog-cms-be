package pl.puzzle.montroe_blog_cms_be.article;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_section.ArticleSection;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.ArticleSummaryItem;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.ArticleTableOfContentItem;

@Service
public class ArticleCreateService {

    private final ArticleRepository articleRepository;

    public ArticleCreateService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Transactional
    public Article createArticle(ArticleCreateRequest request) {
        Article article = Article.createArticle(request);

        for (int i = 0; i < request.summaryItems().size(); i++) {
            ArticleSummaryItem item = ArticleSummaryItem.create(
                    request.summaryItems().get(i),
                    article,
                    i + 1
            );

            article.addSummaryItem(item);
        }

        for (int i = 0; i < request.sections().size(); i++) {
            ArticleSection section = ArticleSection.create(
                    request.sections().get(i),
                    article,
                    i + 1
            );

            article.addSection(section);
        }

        for (int i = 0; i < request.tableOfContentItems().size(); i++) {
            ArticleTableOfContentItem item = ArticleTableOfContentItem.create(
                    request.tableOfContentItems().get(i),
                    article,
                    i + 1
            );

            article.addTableOfContentItem(item);
        }

        return articleRepository.save(article);
    }
}