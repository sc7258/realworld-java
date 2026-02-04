package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.articles.entity.Article;
import com.sc7258.realworldjava.articles.entity.Favorite;
import com.sc7258.realworldjava.articles.entity.Tag;
import com.sc7258.realworldjava.exception.ArticleNotFoundException;
import com.sc7258.realworldjava.exception.ForbiddenException;
import com.sc7258.realworldjava.model.*;
import com.sc7258.realworldjava.users.FollowRepository;
import com.sc7258.realworldjava.users.entity.User;
import com.sc7258.realworldjava.users.UserRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final FavoriteRepository favoriteRepository;
    private final TagRepository tagRepository;

    @Transactional
    public SingleArticleResponse createArticle(NewArticleRequest request, User author) {
        var articleData = request.getArticle();
        Set<Tag> tags = processTags(articleData.getTagList());

        Article article = new Article(
                toSlug(articleData.getTitle()),
                articleData.getTitle(),
                articleData.getDescription(),
                articleData.getBody(),
                author
        );
        article.setTags(tags);

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
        if (articleUpdateData.getTagList() != null) {
            Set<Tag> tags = processTags(articleUpdateData.getTagList());
            article.setTags(tags);
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

            if (StringUtils.hasText(author)) {
                userRepository.findByUsername(author).ifPresent(user ->
                        predicates.add(criteriaBuilder.equal(root.get("author"), user))
                );
            }

            if (StringUtils.hasText(tag)) {
                Join<Article, Tag> tagJoin = root.join("tags");
                predicates.add(criteriaBuilder.equal(tagJoin.get("name"), tag));
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
    
    @Transactional
    public SingleArticleResponse favoriteArticle(String slug, User currentUser) {
        Article article = articleRepository.findBySlug(slug).orElseThrow(() -> new ArticleNotFoundException(slug));
        favoriteRepository.findByUserAndArticle(currentUser, article).orElseGet(() -> {
            Favorite newFavorite = new Favorite(currentUser, article);
            favoriteRepository.save(newFavorite);
            article.getFavoritedBy().add(newFavorite);
            return newFavorite;
        });
        return new SingleArticleResponse().article(mapToArticleModel(article, currentUser));
    }

    @Transactional
    public SingleArticleResponse unfavoriteArticle(String slug, User currentUser) {
        Article article = articleRepository.findBySlug(slug).orElseThrow(() -> new ArticleNotFoundException(slug));
        favoriteRepository.findByUserAndArticle(currentUser, article).ifPresent(favorite -> {
            favoriteRepository.delete(favorite);
            article.getFavoritedBy().remove(favorite);
        });
        return new SingleArticleResponse().article(mapToArticleModel(article, currentUser));
    }

    private Set<Tag> processTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Tag> existingTags = tagRepository.findByNameIn(tagNames);
        Set<String> existingTagNames = existingTags.stream().map(Tag::getName).collect(Collectors.toSet());
        
        Set<Tag> newTags = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(Tag::new)
                .collect(Collectors.toSet());
        
        tagRepository.saveAll(newTags);
        
        existingTags.addAll(newTags);
        return existingTags;
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
        articleModel.setTagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        articleModel.setCreatedAt(article.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()));
        articleModel.setUpdatedAt(article.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()));
        articleModel.setFavorited(article.isFavoritedBy(currentUser));
        articleModel.setFavoritesCount(article.getFavoritesCount());
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
        innerArticle.setTagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        innerArticle.setCreatedAt(article.getCreatedAt().atOffset(OffsetDateTime.now().getOffset()));
        innerArticle.setUpdatedAt(article.getUpdatedAt().atOffset(OffsetDateTime.now().getOffset()));
        innerArticle.setFavorited(article.isFavoritedBy(currentUser));
        innerArticle.setFavoritesCount(article.getFavoritesCount());
        Profile authorProfile = new Profile();
        authorProfile.setUsername(article.getAuthor().getUsername());
        authorProfile.setBio(article.getAuthor().getBio());
        authorProfile.setImage(article.getAuthor().getImage());
        authorProfile.setFollowing(false);
        innerArticle.setAuthor(authorProfile);
        return innerArticle;
    }
}
