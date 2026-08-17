package pl.puzzle.montroe_blog_cms_be.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CmsUserControllerTest {

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
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUser() throws Exception {
        String request = """
                {
                  "email": "new@test.pl",
                  "password": "Password123!"
                }
                """;

        mockMvc.perform(
                        post("/users")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.pl"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnConflictWhenUserAlreadyExists() throws Exception {
        createUser("existing@test.pl", true);

        String request = """
                {
                  "email": "existing@test.pl",
                  "password": "AnotherPassword123!"
                }
                """;

        mockMvc.perform(
                        post("/users")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnAllUsers() throws Exception {
        createUser("first@test.pl", true);
        createUser("second@test.pl", false);

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDisableUser() throws Exception {
        createUser("first@test.pl", true);
        CmsUser secondAdmin = createUser("second@test.pl", true);

        String request = """
                {
                  "enabled": false
                }
                """;

        mockMvc.perform(
                        patch("/users/{id}/enabled", secondAdmin.getId())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnConflictWhenDisablingLastActiveAdmin() throws Exception {
        CmsUser admin = createUser("admin@test.pl", true);

        String request = """
                {
                  "enabled": false
                }
                """;

        mockMvc.perform(
                        patch("/users/{id}/enabled", admin.getId())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isConflict());
    }

    private CmsUser createUser(String email, boolean enabled) {
        CmsUser user = CmsUser.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.ADMIN)
                .enabled(enabled)
                .createdAt(LocalDateTime.now())
                .build();

        return cmsUserRepository.save(user);
    }
}