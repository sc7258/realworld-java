package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.api.FavoritesApi;
import com.sc7258.realworldjava.model.SingleArticleResponse;
import com.sc7258.realworldjava.users.entity.User;
import com.sc7258.realworldjava.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FavoritesController implements FavoritesApi {

    private final ArticleService articleService;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<SingleArticleResponse> createArticleFavorite(String slug) {
        User currentUser = getRequiredCurrentUser();
        return ResponseEntity.ok(articleService.favoriteArticle(slug, currentUser));
    }

    @Override
    public ResponseEntity<SingleArticleResponse> deleteArticleFavorite(String slug) {
        User currentUser = getRequiredCurrentUser();
        return ResponseEntity.ok(articleService.unfavoriteArticle(slug, currentUser));
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
