package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.articles.entity.Article;
import com.sc7258.realworldjava.exception.ArticleNotFoundException;
import com.sc7258.realworldjava.exception.ForbiddenException;
import com.sc7258.realworldjava.model.*;
import com.sc7258.realworldjava.users.FollowRepository;
import com.sc7258.realworldjava.users.entity.User;
import com.sc7258.realworldjava.users.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public SingleArticleResponse createArticle(NewArticleRequest request, User author) {
        Article article = new Article(toSlug(request.getArticle().getTitle()), request.getArticle().getTitle(), request.getArticle().getDescription(), request.getArticle().getBody(), author);
        Article savedArticle = articleRepository.save(article);
        return new SingleArticleResponse().article(mapToArticleModel(savedArticle, author));
    }

    @Transactional(readOnly = true)
    public SingleArticleResponse getArticle(String slug, User currentUser) {
        Article article = articleRepository.findBySlug(slug).orElseThrow(() -> new ArticleNotFoundException(slug));
        return new SingleArticleResponse().article(mapToArticleModel(article, currentUser));
    }

    @Transactional
    public SingleArticleResponse updateArticle(String slug, UpdateArticleRequest request, User currentUser) {
        Article article = articleRepository.findBySlug(slug).orElseThrow(() -> new ArticleNotFoundException(slug));
        if (!Objects.equals(article.getAuthor().getId(), currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to update this article.");
        }
        var articleUpdateData = request.getArticle();
        if (articleUpdateData.getTitle() != null) {
            article.setTitle(articleUpdateData.getTitle());
            article.setSlug(toSlug(articleUpdateData.getTitle()));
        }
        if (articleUpdateData.getDescription() != null) {
            article.setDescription(articleUpdateData.getDescription());
        }
        if (articleUpdateData.getBody() != null) {
            article.setBody(articleUpdateData.getBody());
        }
        Article updatedArticle = articleRepository.save(article);
        return new SingleArticleResponse().article(mapToArticleModel(updatedArticle, currentUser));
    }

    @Transactional
    public void deleteArticle(String slug, User currentUser) {
        Article article = articleRepository.findBySlug(slug).orElseThrow(() -> new ArticleNotFoundException(slug));
        if (!Objects.equals(article.getAuthor().getId(), currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to delete this article.");
        }
        articleRepository.delete(article);
    }

    @Transactional(readOnly = true)
    public MultipleArticlesResponse getArticles(User currentUser, String tag, String author, String favorited, int offset, int limit) {
        Specification<Article> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (author != null) {
                userRepository.findByUsername(author).ifPresent(user ->
                        predicates.add(criteriaBuilder.equal(root.get("author"), user))
                );
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        PageRequest pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlePage = articleRepository.findAll(spec, pageable);
        
        List<MultipleArticlesResponseArticlesInner> innerArticles = articlePage.getContent().stream()
                .map(article -> mapToArticlesInner(article, currentUser))
                .collect(Collectors.toList());

        return new MultipleArticlesResponse().articles(innerArticles).articlesCount((int) articlePage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public MultipleArticlesResponse getArticlesFeed(User currentUser, int offset, int limit) {
        List<User> followedUsers = followRepository.findByFollower(currentUser).stream()
                .map(follow -> follow.getFollowed())
                .collect(Collectors.toList());

        if (followedUsers.isEmpty()) {
            return new MultipleArticlesResponse().articles(Collections.emptyList()).articlesCount(0);
        }

        Specification<Article> spec = (root, query, criteriaBuilder) -> root.get("author").in(followedUsers);
        PageRequest pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlePage = articleRepository.findAll(spec, pageable);

        List<MultipleArticlesResponseArticlesInner> innerArticles = articlePage.getContent().stream()
                .map(article -> mapToArticlesInner(article, currentUser))
                .collect(Collectors.toList());

        return new MultipleArticlesResponse().articles(innerArticles).articlesCount((int) articlePage.getTotalElements());
    }

    private String toSlug(String title) {
        return title.toLowerCase().replaceAll("[\\&|\\/|\\s|\\,]", "-");
    }

    private com.sc7258.realworldjava.model.Article mapToArticleModel(Article article, User currentUser) {
        com.sc7258.realworldjava.model.Article articleModel = new com.sc7258.realworldjava.model.Article();
        articleModel.setSlug(article.getSlug());
        articleModel.setTitle(article.getTitle());
        articleModel.setDescription(article.getDescription());
        articleModel.setBody(article.getBody());
        articleModel.setTagList(Collections.emptyList());
        articleModel.setCreatedAt(article.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()));
        articleModel.setUpdatedAt(article.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()));
        articleModel.setFavorited(false);
        articleModel.setFavoritesCount(0);
        Profile authorProfile = new Profile();
        authorProfile.setUsername(article.getAuthor().getUsername());
        authorProfile.setBio(article.getAuthor().getBio());
        authorProfile.setImage(article.getAuthor().getImage());
        authorProfile.setFollowing(false);
        articleModel.setAuthor(authorProfile);
        return articleModel;
    }

    private MultipleArticlesResponseArticlesInner mapToArticlesInner(Article article, User currentUser) {
        MultipleArticlesResponseArticlesInner innerArticle = new MultipleArticlesResponseArticlesInner();
        innerArticle.setSlug(article.getSlug());
        innerArticle.setTitle(article.getTitle());
        innerArticle.setDescription(article.getDescription());
        // innerArticle.setBody(article.getBody()); // This line caused the error and is now removed.
        innerArticle.setTagList(Collections.emptyList());
        innerArticle.setCreatedAt(article.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()));
        innerArticle.setUpdatedAt(article.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()));
        innerArticle.setFavorited(false);
        innerArticle.setFavoritesCount(0);
        Profile authorProfile = new Profile();
        authorProfile.setUsername(article.getAuthor().getUsername());
        authorProfile.setBio(article.getAuthor().getBio());
        authorProfile.setImage(article.getAuthor().getImage());
        authorProfile.setFollowing(false);
        innerArticle.setAuthor(authorProfile);
        return innerArticle;
    }
}
