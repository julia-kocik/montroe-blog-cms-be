package pl.puzzle.montroe_blog_cms_be.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.puzzle.montroe_blog_cms_be.exception.UserAlreadyExistsException;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserCreateRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class CmsUserCreateServiceTest {

    @Autowired
    private CmsUserCreateService cmsUserCreateService;

    @Autowired
    private CmsUserRepository cmsUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        cmsUserRepository.deleteAll();
    }

    @Test
    void shouldCreateAdminUser() {
        CmsUser user = cmsUserCreateService.createUser(
                new CmsUserCreateRequest(
                        "second@test.pl",
                        "Password123!"
                )
        );

        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("second@test.pl");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.isEnabled()).isTrue();

        assertThat(
                passwordEncoder.matches(
                        "Password123!",
                        user.getPassword()
                )
        ).isTrue();

        assertThat(user.getPassword())
                .isNotEqualTo("Password123!");
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        cmsUserCreateService.createUser(
                new CmsUserCreateRequest(
                        "second@test.pl",
                        "Password123!"
                )
        );

        assertThatThrownBy(() ->
                cmsUserCreateService.createUser(
                        new CmsUserCreateRequest(
                                "second@test.pl",
                                "AnotherPassword123!"
                        )
                )
        )
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User already exists");
    }
}