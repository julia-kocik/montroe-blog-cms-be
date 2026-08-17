package pl.puzzle.montroe_blog_cms_be.article;

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
import pl.puzzle.montroe_blog_cms_be.article.dto.ArticleCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_section.dto.ArticleSectionCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_summary_item.dto.ArticleSummaryItemCreateRequest;
import pl.puzzle.montroe_blog_cms_be.article_table_of_content_item.dto.ArticleTableOfContentItemCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.UUID;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.hamcrest.Matchers.hasItems;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArticleControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ArticleCreateService articleCreateService;

    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();

        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateArticle() throws Exception {
        String request = """
                {
                    "name": "Test article",
                    "lead": "Test lead",
                    "summaryItems": [
                        {
                            "name": "First summary"
                        }
                    ],
                    "sections": [
                        {
                            "subHeading": "First section",
                            "paragraph": "Test paragraph",
                            "imageLarge": "",
                            "imageSmall": ""
                        }
                    ],
                    "tableOfContentItems": [
                        {
                            "name": "First section"
                        }
                    ]
                }
                """;

        mockMvc.perform(
                        post("/article")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Test article"))
                .andExpect(jsonPath("$.lead")
                        .value("Test lead"))
                .andExpect(jsonPath("$.path")
                        .value("test-article"))
                .andExpect(jsonPath("$.summaryItems.length()")
                        .value(1))
                .andExpect(jsonPath("$.sections.length()")
                        .value(1))
                .andExpect(jsonPath("$.tableOfContentItems.length()")
                        .value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetArticleById() throws Exception {
        Article article = articleCreateService.createArticle(
                new ArticleCreateRequest(
                        "Test article",
                        "Test lead",
                        List.of(
                                new ArticleSummaryItemCreateRequest(
                                        "First summary"
                                )
                        ),
                        List.of(
                                new ArticleSectionCreateRequest(
                                        "First section",
                                        "Test paragraph",
                                        "",
                                        ""
                                )
                        ),
                        List.of(
                                new ArticleTableOfContentItemCreateRequest(
                                        "First section"
                                )
                        )
                )
        );

        mockMvc.perform(
                        get("/article/{id}", article.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(article.getId().toString()))
                .andExpect(jsonPath("$.name")
                        .value("Test article"))
                .andExpect(jsonPath("$.lead")
                        .value("Test lead"))
                .andExpect(jsonPath("$.summaryItems.length()")
                        .value(1))
                .andExpect(jsonPath("$.sections.length()")
                        .value(1))
                .andExpect(jsonPath("$.tableOfContentItems.length()")
                        .value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllArticles() throws Exception {
        articleCreateService.createArticle(
                new ArticleCreateRequest(
                        "First article",
                        "First lead",
                        List.of(
                                new ArticleSummaryItemCreateRequest(
                                        "Summary"
                                )
                        ),
                        List.of(
                                new ArticleSectionCreateRequest(
                                        "Section",
                                        "Paragraph",
                                        "",
                                        ""
                                )
                        ),
                        List.of(
                                new ArticleTableOfContentItemCreateRequest(
                                        "Section"
                                )
                        )
                )
        );

        articleCreateService.createArticle(
                new ArticleCreateRequest(
                        "Second article",
                        "Second lead",
                        List.of(
                                new ArticleSummaryItemCreateRequest(
                                        "Summary"
                                )
                        ),
                        List.of(
                                new ArticleSectionCreateRequest(
                                        "Section",
                                        "Paragraph",
                                        "",
                                        ""
                                )
                        ),
                        List.of(
                                new ArticleTableOfContentItemCreateRequest(
                                        "Section"
                                )
                        )
                )
        );

        mockMvc.perform(
                        get("/article")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name")
                        .value(hasItems(
                                "First article",
                                "Second article"
                        )))
                .andExpect(jsonPath("$[*].lead")
                        .value(hasItems(
                                "First lead",
                                "Second lead"
                        )));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateArticle() throws Exception {
        Article article = articleCreateService.createArticle(
                new ArticleCreateRequest(
                        "Old name",
                        "Old lead",
                        List.of(
                                new ArticleSummaryItemCreateRequest(
                                        "Old summary"
                                )
                        ),
                        List.of(
                                new ArticleSectionCreateRequest(
                                        "Old section",
                                        "Old paragraph",
                                        "",
                                        ""
                                )
                        ),
                        List.of(
                                new ArticleTableOfContentItemCreateRequest(
                                        "Old section"
                                )
                        )
                )
        );

        UUID summaryItemId =
                article.getSummaryItems().getFirst().getId();

        String request = """
            {
                "name": "New name",
                "summaryItems": [
                    {
                        "id": "%s",
                        "name": "Updated summary"
                    },
                    {
                        "name": "New summary"
                    }
                ]
            }
            """.formatted(summaryItemId);

        mockMvc.perform(
                        patch("/article/{id}", article.getId())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("New name"))
                .andExpect(jsonPath("$.path")
                        .value("new-name"))


                .andExpect(jsonPath("$.lead")
                        .value("Old lead"))

                .andExpect(jsonPath("$.summaryItems.length()")
                        .value(2))

                .andExpect(jsonPath("$.summaryItems[0].id")
                        .value(summaryItemId.toString()))
                .andExpect(jsonPath("$.summaryItems[0].name")
                        .value("Updated summary"))
                .andExpect(jsonPath("$.summaryItems[0].position")
                        .value(1))

                .andExpect(jsonPath("$.summaryItems[1].name")
                        .value("New summary"))
                .andExpect(jsonPath("$.summaryItems[1].position")
                        .value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteArticle() throws Exception {
        Article article = articleCreateService.createArticle(
                new ArticleCreateRequest(
                        "Article to delete",
                        "Lead",
                        List.of(
                                new ArticleSummaryItemCreateRequest(
                                        "Summary"
                                )
                        ),
                        List.of(
                                new ArticleSectionCreateRequest(
                                        "Section",
                                        "Paragraph",
                                        "",
                                        ""
                                )
                        ),
                        List.of(
                                new ArticleTableOfContentItemCreateRequest(
                                        "Section"
                                )
                        )
                )
        );

        UUID articleId = article.getId();

        mockMvc.perform(
                        delete("/article/{id}", articleId)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        assertThat(articleRepository.findById(articleId))
                .isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteAllArticles() throws Exception {
        articleCreateService.createArticle(
                new ArticleCreateRequest(
                        "First article",
                        "First lead",
                        List.of(
                                new ArticleSummaryItemCreateRequest(
                                        "Summary"
                                )
                        ),
                        List.of(
                                new ArticleSectionCreateRequest(
                                        "Section",
                                        "Paragraph",
                                        "",
                                        ""
                                )
                        ),
                        List.of(
                                new ArticleTableOfContentItemCreateRequest(
                                        "Section"
                                )
                        )
                )
        );

        articleCreateService.createArticle(
                new ArticleCreateRequest(
                        "Second article",
                        "Second lead",
                        List.of(
                                new ArticleSummaryItemCreateRequest(
                                        "Summary"
                                )
                        ),
                        List.of(
                                new ArticleSectionCreateRequest(
                                        "Section",
                                        "Paragraph",
                                        "",
                                        ""
                                )
                        ),
                        List.of(
                                new ArticleTableOfContentItemCreateRequest(
                                        "Section"
                                )
                        )
                )
        );

        assertThat(articleRepository.findAll())
                .hasSize(2);

        mockMvc.perform(
                        delete("/article")
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        assertThat(articleRepository.findAll())
                .isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenCreateRequestIsInvalid() throws Exception {
        String request = """
            {
                "name": "",
                "lead": "",
                "summaryItems": [],
                "sections": [],
                "tableOfContentItems": []
            }
            """;

        mockMvc.perform(
                        post("/article")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        assertThat(articleRepository.findAll())
                .isEmpty();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenArticleDoesNotExist() throws Exception {
        UUID articleId = UUID.randomUUID();

        mockMvc.perform(
                        get("/article/{id}", articleId)
                )
                .andExpect(status().isNotFound());
    }

}