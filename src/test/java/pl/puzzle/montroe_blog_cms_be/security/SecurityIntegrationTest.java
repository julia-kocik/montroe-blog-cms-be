package pl.puzzle.montroe_blog_cms_be.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import pl.puzzle.montroe_blog_cms_be.article.ArticleRepository;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ArticleRepository articleRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();

        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAuthenticateMockUser() throws Exception {
        mockMvc.perform(
                        get("/article")
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticated()
            throws Exception {

        mockMvc.perform(
                        get("/article")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenUserIsNotAdmin()
            throws Exception {

        mockMvc.perform(
                        get("/article")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnForbiddenWhenCsrfTokenIsMissing()
            throws Exception {

        String request = """
                {
                    "name": "Test article",
                    "lead": "Test lead",
                    "summaryItems": [
                        {
                            "name": "Summary"
                        }
                    ],
                    "sections": [
                        {
                            "subHeading": "Section",
                            "paragraph": "Paragraph",
                            "imageLarge": "",
                            "imageSmall": ""
                        }
                    ],
                    "tableOfContentItems": [
                        {
                            "name": "Section"
                        }
                    ]
                }
                """;

        mockMvc.perform(
                        post("/article")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void shouldReturnUnauthorizedForUsersEndpointWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenForUsersEndpointWithoutAdminRole()
            throws Exception {

        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isForbidden());
    }
}