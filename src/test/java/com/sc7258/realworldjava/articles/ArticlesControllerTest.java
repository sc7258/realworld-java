package com.sc7258.realworldjava.articles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sc7258.realworldjava.articles.entity.Article;
import com.sc7258.realworldjava.articles.entity.Tag;
import com.sc7258.realworldjava.model.UpdateArticle;
import com.sc7258.realworldjava.model.UpdateArticleRequest;
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

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
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
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        // 각 테스트가 독립적으로 데이터를 생성하도록 이 메소드는 비워둡니다.
        // @Transactional이 테스트 후 모든 데이터를 롤백합니다.
    }

    @Test
    @WithMockUser(username = "user1")
    void createArticle_withTags() throws Exception {
        // given: 이 테스트는 인증된 사용자가 필요하므로, DB에 해당 사용자를 미리 저장합니다.
        userRepository.saveAndFlush(new User("user1@example.com", "user1", "password"));
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

        // when & then
        mockMvc.perform(post("/api/articles").contentType(MediaType.APPLICATION_JSON).content(newArticleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.article.tagList", containsInAnyOrder("java", "spring")));
    }

    @Test
    @WithMockUser(username = "user1")
    void updateArticle_withTags() throws Exception {
        // given: 이 테스트는 수정할 대상 게시글이 필요합니다.
        User user = userRepository.saveAndFlush(new User("user1@example.com", "user1", "password"));
        Tag tag1 = tagRepository.save(new Tag("java"));
        Tag tag2 = tagRepository.save(new Tag("spring"));
        Article article = new Article("test-article", "Test Article", "d", "b", user);
        article.setTags(Set.of(tag1, tag2));
        Article savedArticle = articleRepository.saveAndFlush(article);

        UpdateArticle articleUpdate = new UpdateArticle();
        articleUpdate.setTagList(List.of("java", "testing", "new-tag"));
        UpdateArticleRequest request = new UpdateArticleRequest();
        request.setArticle(articleUpdate);

        // when & then
        mockMvc.perform(put("/api/articles/{slug}", savedArticle.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.slug").value(savedArticle.getSlug()))
                .andExpect(jsonPath("$.article.tagList", containsInAnyOrder("java", "testing", "new-tag")));
    }

    @Test
    void getArticles_byTag() throws Exception {
        // given: 이 테스트는 필터링할 여러 게시글이 필요합니다.
        User user = userRepository.saveAndFlush(new User("user1@example.com", "user1", "password"));
        Tag javaTag = tagRepository.save(new Tag("java"));
        Tag springTag = tagRepository.save(new Tag("spring"));

        Article article1 = new Article("article-1", "Article 1", "d", "b", user);
        article1.setTags(Set.of(javaTag, springTag));
        articleRepository.save(article1);

        Article article2 = new Article("article-2", "Article 2", "d", "b", user);
        article2.setTags(Set.of(javaTag));
        articleRepository.saveAndFlush(article2);

        // when & then
        mockMvc.perform(get("/api/articles?tag=spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1))
                .andExpect(jsonPath("$.articles[0].slug").value("article-1"));
    }
}
