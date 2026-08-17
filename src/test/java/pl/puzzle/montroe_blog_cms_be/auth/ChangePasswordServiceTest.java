package pl.puzzle.montroe_blog_cms_be.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.puzzle.montroe_blog_cms_be.auth.dto.ChangePasswordRequest;
import pl.puzzle.montroe_blog_cms_be.exception.InvalidPasswordException;
import pl.puzzle.montroe_blog_cms_be.user.CmsUser;
import pl.puzzle.montroe_blog_cms_be.user.CmsUserRepository;
import pl.puzzle.montroe_blog_cms_be.user.Role;

import java.time.LocalDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ChangePasswordServiceTest {

    @Autowired
    private ChangePasswordService changePasswordService;

    @Autowired
    private CmsUserRepository cmsUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        cmsUserRepository.deleteAll();
    }

    @Test
    void shouldChangePassword() {
        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email("admin@test.pl")
                .password(passwordEncoder.encode("OldPassword123!"))
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        cmsUserRepository.save(user);

        changePasswordService.changePassword(
                "admin@test.pl",
                new ChangePasswordRequest(
                        "OldPassword123!",
                        "NewPassword123!"
                )
        );

        CmsUser updatedUser = cmsUserRepository
                .findByEmail("admin@test.pl")
                .orElseThrow();

        assertThat(
                passwordEncoder.matches(
                        "NewPassword123!",
                        updatedUser.getPassword()
                )
        ).isTrue();

        assertThat(
                passwordEncoder.matches(
                        "OldPassword123!",
                        updatedUser.getPassword()
                )
        ).isFalse();

        assertThat(updatedUser.getPassword())
                .isNotEqualTo("NewPassword123!");
    }

    @Test
    void shouldNotChangePasswordWhenCurrentPasswordIsIncorrect() {
        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email("admin@test.pl")
                .password(passwordEncoder.encode("OldPassword123!"))
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        cmsUserRepository.save(user);

        assertThatThrownBy(() ->
                changePasswordService.changePassword(
                        "admin@test.pl",
                        new ChangePasswordRequest(
                                "WrongPassword!",
                                "NewPassword123!"
                        )
                )
        )
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Current password is incorrect");

        CmsUser unchangedUser = cmsUserRepository
                .findByEmail("admin@test.pl")
                .orElseThrow();

        assertThat(
                passwordEncoder.matches(
                        "OldPassword123!",
                        unchangedUser.getPassword()
                )
        ).isTrue();

        assertThat(
                passwordEncoder.matches(
                        "NewPassword123!",
                        unchangedUser.getPassword()
                )
        ).isFalse();
    }
}