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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TagsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        User user = userRepository.saveAndFlush(new User("user@example.com", "user", "password"));

        Tag tag1 = tagRepository.save(new Tag("java"));
        Tag tag2 = tagRepository.save(new Tag("spring"));
        Tag tag3 = tagRepository.save(new Tag("testing"));
        tagRepository.flush();

        Article article1 = new Article("article-1", "Article 1", "d", "b", user);
        article1.setTags(Set.of(tag1, tag2));
        articleRepository.save(article1);

        Article article2 = new Article("article-2", "Article 2", "d", "b", user);
        article2.setTags(Set.of(tag1, tag3));
        articleRepository.save(article2);
        
        articleRepository.flush();
    }

    @Test
    void getTags_success() throws Exception {
        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", containsInAnyOrder("java", "spring", "testing")));
    }
}
