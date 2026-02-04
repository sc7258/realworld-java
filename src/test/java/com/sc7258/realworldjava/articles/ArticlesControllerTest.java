package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.articles.entity.Article;
import com.sc7258.realworldjava.articles.entity.Tag;
import com.sc7258.realworldjava.users.entity.User;
import com.sc7258.realworldjava.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticlesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private TagRepository tagRepository;

    private User savedUser1;

    @BeforeEach
    void setUp() {
        savedUser1 = userRepository.saveAndFlush(new User("user1@example.com", "user1", "password"));
    }

    @Test
    @WithMockUser(username = "user1")
    void createArticle_withTags() throws Exception {
        String newArticleJson = """
                {
                  "article": {
                    "title": "Test With Tags",
                    "description": "Desc",
                    "body": "Body",
                    "tagList": ["java", "spring"]
                  }
                }
                """;
        mockMvc.perform(post("/api/articles").contentType(MediaType.APPLICATION_JSON).content(newArticleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.article.tagList", containsInAnyOrder("java", "spring")));
    }
    
    @Test
    void getArticles_byTag() throws Exception {
        // given
        Tag javaTag = tagRepository.save(new Tag("java"));
        Tag springTag = tagRepository.save(new Tag("spring"));
        tagRepository.flush();

        Article article1 = new Article("article-1", "Article 1", "d", "b", savedUser1);
        article1.setTags(Set.of(javaTag, springTag));
        articleRepository.save(article1);

        Article article2 = new Article("article-2", "Article 2", "d", "b", savedUser1);
        article2.setTags(Set.of(javaTag));
        articleRepository.save(article2);
        
        articleRepository.flush();

        // when & then
        mockMvc.perform(get("/api/articles?tag=spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1))
                .andExpect(jsonPath("$.articles[0].slug").value("article-1"));
    }

    // --- Other tests...
}
