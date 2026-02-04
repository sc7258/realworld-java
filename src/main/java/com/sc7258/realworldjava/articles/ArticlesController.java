package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.api.ArticlesApi;
import com.sc7258.realworldjava.model.MultipleArticlesResponse;
import com.sc7258.realworldjava.model.NewArticleRequest;
import com.sc7258.realworldjava.model.SingleArticleResponse;
import com.sc7258.realworldjava.model.UpdateArticleRequest;
import com.sc7258.realworldjava.users.entity.User;
import com.sc7258.realworldjava.users.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArticlesController implements ArticlesApi {

    private final ArticleService articleService;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<SingleArticleResponse> createArticle(@Valid NewArticleRequest newArticleRequest) {
        User currentUser = getRequiredCurrentUser();
        SingleArticleResponse articleResponse = articleService.createArticle(newArticleRequest, currentUser);
        return new ResponseEntity<>(articleResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteArticle(String slug) {
        User currentUser = getRequiredCurrentUser();
        articleService.deleteArticle(slug, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<SingleArticleResponse> getArticle(String slug) {
        User currentUser = getOptionalCurrentUser().orElse(null);
        SingleArticleResponse articleResponse = articleService.getArticle(slug, currentUser);
        return ResponseEntity.ok(articleResponse);
    }

    @Override
    public ResponseEntity<MultipleArticlesResponse> getArticles(String tag, String author, String favorited, Integer offset, Integer limit) {
        User currentUser = getOptionalCurrentUser().orElse(null);
        int realOffset = (offset != null) ? offset : 0;
        int realLimit = (limit != null) ? limit : 20;
        MultipleArticlesResponse response = articleService.getArticles(currentUser, tag, author, favorited, realOffset, realLimit);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MultipleArticlesResponse> getArticlesFeed(Integer offset, Integer limit) {
        User currentUser = getRequiredCurrentUser();
        int realOffset = (offset != null) ? offset : 0;
        int realLimit = (limit != null) ? limit : 20;
        MultipleArticlesResponse response = articleService.getArticlesFeed(currentUser, realOffset, realLimit);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SingleArticleResponse> updateArticle(String slug, @Valid UpdateArticleRequest updateArticleRequest) {
        User currentUser = getRequiredCurrentUser();
        SingleArticleResponse articleResponse = articleService.updateArticle(slug, updateArticleRequest, currentUser);
        return ResponseEntity.ok(articleResponse);
    }

    private User getRequiredCurrentUser() {
        return getOptionalCurrentUser()
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 가져올 수 없습니다."));
    }

    private Optional<User> getOptionalCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }
}
