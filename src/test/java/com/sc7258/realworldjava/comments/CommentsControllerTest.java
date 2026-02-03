package com.sc7258.realworldjava.comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sc7258.realworldjava.articles.ArticleRepository;
import com.sc7258.realworldjava.articles.entity.Article;
import com.sc7258.realworldjava.comments.entity.Comment;
import com.sc7258.realworldjava.model.NewCommentRequest;
import com.sc7258.realworldjava.users.UserRepository;
import com.sc7258.realworldjava.users.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User otherUser;
    private Article testArticle;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        // 1. 자식 테이블부터 역순으로 삭제 (참조 무결성 유지)
        commentRepository.deleteAllInBatch();
        articleRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        String suffix = String.valueOf(System.currentTimeMillis()).substring(8);

        // 2. 유저 저장 및 영속화된 객체 다시 받기 (고정된 username 사용)
        User u1 = new User("test" + suffix + "@example.com", "testuser", passwordEncoder.encode("password"));
        User u2 = new User("other" + suffix + "@example.com", "otheruser", passwordEncoder.encode("password"));
        
        testUser = userRepository.saveAndFlush(u1); // 반환된 객체를 사용해야 ID가 확실히 존재함
        otherUser = userRepository.saveAndFlush(u2);

        // 3. 게시글 저장
        testArticle = new Article(
            "slug-" + suffix, 
            "Title " + suffix, 
            "Description", 
            "Body", 
            testUser
        );
        testArticle = articleRepository.saveAndFlush(testArticle);

        // 4. 댓글 저장
        testComment = new Comment("Comment content", testUser, testArticle);
        testComment = commentRepository.saveAndFlush(testComment);
    }

    @Test
    @WithMockUser(username = "testuser")
    void addComment_Success() throws Exception {
        var newComment = new com.sc7258.realworldjava.model.NewComment().body("A new comment.");
        var request = new NewCommentRequest().comment(newComment);

        mockMvc.perform(post("/api/articles/{slug}/comments", testArticle.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment.body").value("A new comment."))
                .andExpect(jsonPath("$.comment.author.username").value(testUser.getUsername()));
    }

    @Test
    void addComment_Unauthorized() throws Exception {
        var newComment = new com.sc7258.realworldjava.model.NewComment().body("A new comment.");
        var request = new NewCommentRequest().comment(newComment);

        mockMvc.perform(post("/api/articles/{slug}/comments", testArticle.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void addComment_ArticleNotFound() throws Exception {
        var newComment = new com.sc7258.realworldjava.model.NewComment().body("A new comment.");
        var request = new NewCommentRequest().comment(newComment);

        mockMvc.perform(post("/api/articles/{slug}/comments", "non-existent-slug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getComments_Success() throws Exception {
        mockMvc.perform(get("/api/articles/{slug}/comments", testArticle.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].body").value(testComment.getBody()));
    }

    @Test
    void getComments_ArticleNotFound() throws Exception {
        mockMvc.perform(get("/api/articles/{slug}/comments", "non-existent-slug"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteComment_Success() throws Exception {
        mockMvc.perform(delete("/api/articles/{slug}/comments/{id}", testArticle.getSlug(), testComment.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteComment_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/articles/{slug}/comments/{id}", testArticle.getSlug(), testComment.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "otheruser")
    void deleteComment_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/articles/{slug}/comments/{id}", testArticle.getSlug(), testComment.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteComment_CommentNotFound() throws Exception {
        mockMvc.perform(delete("/api/articles/{slug}/comments/{id}", testArticle.getSlug(), 9999L))
                .andExpect(status().isNotFound());
    }
}
