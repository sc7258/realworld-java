package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.articles.entity.Article;
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

    private User savedUser1;
    private User savedUser2;

    @BeforeEach
    void setUp() {
        savedUser1 = userRepository.saveAndFlush(new User("user1@example.com", "user1", "password"));
        savedUser2 = userRepository.saveAndFlush(new User("user2@example.com", "user2", "password"));
    }

    @Test
    @WithMockUser(username = "user1")
    void createArticle_success() throws Exception {
        String newArticleJson = """
                { "article": { "title": "Test Article", "description": "Desc", "body": "Body" } }
                """;
        mockMvc.perform(post("/api/articles").contentType(MediaType.APPLICATION_JSON).content(newArticleJson))
                .andExpect(status().isCreated());
    }

    @Test
    void getArticle_success() throws Exception {
        Article article = new Article("my-test-article", "My Test Article", "description", "body", savedUser1);
        articleRepository.saveAndFlush(article);
        mockMvc.perform(get("/api/articles/my-test-article"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1")
    void updateArticle_success() throws Exception {
        Article originalArticle = new Article("my-test-article", "title", "desc", "body", savedUser1);
        articleRepository.saveAndFlush(originalArticle);
        String updateJson = """
                { "article": { "body": "updated body" } }
                """;
        mockMvc.perform(put("/api/articles/my-test-article").contentType(MediaType.APPLICATION_JSON).content(updateJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1")
    void deleteArticle_success() throws Exception {
        Article article = new Article("my-test-article", "title", "desc", "body", savedUser1);
        articleRepository.saveAndFlush(article);

        mockMvc.perform(delete("/api/articles/my-test-article"))
                .andExpect(status().isNoContent());

        assertFalse(articleRepository.existsBySlug("my-test-article"));
    }

    @Test
    void getArticles_success() throws Exception {
        articleRepository.save(new Article("article-1", "Article 1", "desc 1", "body 1", savedUser1));
        articleRepository.save(new Article("article-2", "Article 2", "desc 2", "body 2", savedUser2));
        articleRepository.flush();

        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(2));
    }
}
