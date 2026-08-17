package pl.puzzle.montroe_blog_cms_be.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CmsUserRepository extends JpaRepository<CmsUser, UUID> {

    Optional<CmsUser> findByEmail(String email);

    long countByRoleAndEnabledTrue(Role role);
}