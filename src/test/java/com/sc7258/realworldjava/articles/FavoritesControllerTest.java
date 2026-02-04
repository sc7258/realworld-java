package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.articles.entity.Article;
import com.sc7258.realworldjava.articles.entity.Favorite;
import com.sc7258.realworldjava.users.entity.User;
import com.sc7258.realworldjava.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private User savedUser1;
    private User savedUser2;
    private Article savedArticle1;

    @BeforeEach
    void setUp() {
        savedUser1 = userRepository.saveAndFlush(new User("user1@example.com", "user1", "password"));
        savedUser2 = userRepository.saveAndFlush(new User("user2@example.com", "user2", "password"));
        savedArticle1 = articleRepository.saveAndFlush(new Article("test-article", "Test Article", "desc", "body", savedUser1));
    }

    @Test
    @WithMockUser(username = "user2")
    void favoriteArticle_success() throws Exception {
        mockMvc.perform(post("/api/articles/test-article/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.favorited").value(true))
                .andExpect(jsonPath("$.article.favoritesCount").value(1));
    }

    @Test
    @WithMockUser(username = "user2")
    void unfavoriteArticle_success() throws Exception {
        // given: user2가 test-article을 미리 좋아요 해놓음
        favoriteRepository.saveAndFlush(new Favorite(savedUser2, savedArticle1));

        mockMvc.perform(delete("/api/articles/test-article/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.favorited").value(false))
                .andExpect(jsonPath("$.article.favoritesCount").value(0));
    }
}
