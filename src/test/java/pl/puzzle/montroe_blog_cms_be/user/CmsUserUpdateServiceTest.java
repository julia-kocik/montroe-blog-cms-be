package pl.puzzle.montroe_blog_cms_be.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.puzzle.montroe_blog_cms_be.exception.LastActiveAdminException;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserEnabledUpdateRequest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class CmsUserUpdateServiceTest {

    @Autowired
    private CmsUserUpdateService cmsUserUpdateService;

    @Autowired
    private CmsUserRepository cmsUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        cmsUserRepository.deleteAll();
    }

    @Test
    void shouldDisableAdminWhenAnotherActiveAdminExists() {
        CmsUser firstAdmin = createUser(
                "first@test.pl",
                true
        );

        createUser(
                "second@test.pl",
                true
        );

        cmsUserUpdateService.updateEnabled(
                firstAdmin.getId(),
                new CmsUserEnabledUpdateRequest(false)
        );

        CmsUser updatedUser = cmsUserRepository
                .findById(firstAdmin.getId())
                .orElseThrow();

        assertThat(updatedUser.isEnabled()).isFalse();
    }

    @Test
    void shouldNotDisableLastActiveAdmin() {
        CmsUser admin = createUser(
                "admin@test.pl",
                true
        );

        assertThatThrownBy(() ->
                cmsUserUpdateService.updateEnabled(
                        admin.getId(),
                        new CmsUserEnabledUpdateRequest(false)
                )
        )
                .isInstanceOf(LastActiveAdminException.class)
                .hasMessage(
                        "Cannot disable the last active admin"
                );

        CmsUser unchangedUser = cmsUserRepository
                .findById(admin.getId())
                .orElseThrow();

        assertThat(unchangedUser.isEnabled()).isTrue();
    }

    @Test
    void shouldEnableDisabledAdmin() {
        CmsUser admin = createUser(
                "admin@test.pl",
                false
        );

        cmsUserUpdateService.updateEnabled(
                admin.getId(),
                new CmsUserEnabledUpdateRequest(true)
        );

        CmsUser updatedUser = cmsUserRepository
                .findById(admin.getId())
                .orElseThrow();

        assertThat(updatedUser.isEnabled()).isTrue();
    }

    private CmsUser createUser(
            String email,
            boolean enabled
    ) {
        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(
                        passwordEncoder.encode("Password123!")
                )
                .role(Role.ADMIN)
                .enabled(enabled)
                .createdAt(LocalDateTime.now())
                .build();

        return cmsUserRepository.save(user);
    }
}