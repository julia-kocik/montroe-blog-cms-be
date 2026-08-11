package pl.puzzle.montroe_blog_cms_be.article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {
}
