package pl.puzzle.montroe_blog_cms_be.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import pl.puzzle.montroe_blog_cms_be.user.CmsUser;
import pl.puzzle.montroe_blog_cms_be.user.CmsUserRepository;
import pl.puzzle.montroe_blog_cms_be.user.Role;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.springframework.mock.web.MockHttpSession;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CmsUserRepository cmsUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        cmsUserRepository.deleteAll();

        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldLoginWithCorrectCredentials() throws Exception {
        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email("admin@test.pl")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        cmsUserRepository.save(user);

        String request = """
                {
                    "email": "admin@test.pl",
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value("admin@test.pl"))
                .andExpect(jsonPath("$.role")
                        .value("ADMIN"));
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsIncorrect() throws Exception {
        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email("admin@test.pl")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        cmsUserRepository.save(user);

        String request = """
            {
                "email": "admin@test.pl",
                "password": "WrongPassword!"
            }
            """;

        mockMvc.perform(
                        post("/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldKeepUserAuthenticatedInSessionAfterLogin() throws Exception {
        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email("admin@test.pl")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        cmsUserRepository.save(user);

        String request = """
            {
                "email": "admin@test.pl",
                "password": "Password123!"
            }
            """;

        MockHttpSession session = (MockHttpSession) mockMvc.perform(
                        post("/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);

        mockMvc.perform(
                        get("/auth/me")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value("admin@test.pl"))
                .andExpect(jsonPath("$.role")
                        .value("ADMIN"));
    }

    @Test
    void shouldLogoutUserAndInvalidateSession() throws Exception {
        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email("admin@test.pl")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        cmsUserRepository.save(user);

        String loginRequest = """
            {
                "email": "admin@test.pl",
                "password": "Password123!"
            }
            """;

        MockHttpSession session = (MockHttpSession) mockMvc.perform(
                        post("/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);

        mockMvc.perform(
                        post("/auth/logout")
                                .with(csrf())
                                .session(session)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/auth/me")
                                .session(session)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenGettingCurrentUserWithoutLogin()
            throws Exception {

        mockMvc.perform(
                        get("/auth/me")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsDisabled()
            throws Exception {

        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email("disabled@test.pl")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.ADMIN)
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .build();

        cmsUserRepository.save(user);

        String request = """
            {
                "email": "disabled@test.pl",
                "password": "Password123!"
            }
            """;

        mockMvc.perform(
                        post("/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "admin@test.pl",
            roles = "ADMIN"
    )
    void shouldReturnBadRequestWhenCurrentPasswordIsIncorrect()
            throws Exception {

        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email("admin@test.pl")
                .password(passwordEncoder.encode("OldPassword123!"))
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        cmsUserRepository.save(user);

        String request = """
            {
                "currentPassword": "WrongPassword!",
                "newPassword": "NewPassword123!"
            }
            """;

        mockMvc.perform(
                        patch("/auth/password")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }
}