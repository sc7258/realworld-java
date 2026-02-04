package com.sc7258.realworldjava.articles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sc7258.realworldjava.articles.entity.Article;
import com.sc7258.realworldjava.users.entity.Follow;
import com.sc7258.realworldjava.users.entity.User;
import com.sc7258.realworldjava.users.FollowRepository;
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

    @Autowired
    private FollowRepository followRepository;

    private User savedUser1;
    private User savedUser2;

    @BeforeEach
    void setUp() {
        savedUser1 = userRepository.saveAndFlush(new User("user1@example.com", "user1", "password"));
        savedUser2 = userRepository.saveAndFlush(new User("user2@example.com", "user2", "password"));
    }

    // --- 게시글 피드 (Feed) ---
    @Test
    @WithMockUser(username = "user1")
    void getArticlesFeed_success() throws Exception {
        // given: user1이 user2를 팔로우
        followRepository.save(new Follow(savedUser1, savedUser2));
        
        // and: user1과 user2가 각각 게시글 작성
        articleRepository.save(new Article("article-by-user1", "Article by user1", "d", "b", savedUser1));
        articleRepository.save(new Article("article-by-user2", "Article by user2", "d", "b", savedUser2));
        articleRepository.flush();

        // when & then: user1의 피드에는 user2의 게시글만 보여야 함
        mockMvc.perform(get("/api/articles/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1))
                .andExpect(jsonPath("$.articles[0].slug").value("article-by-user2"));
    }

    // --- Other tests...
}
