package com.sc7258.realworldjava.comments;

import com.sc7258.realworldjava.articles.ArticleRepository;
import com.sc7258.realworldjava.articles.entity.Article;
import com.sc7258.realworldjava.comments.entity.Comment;
import com.sc7258.realworldjava.exception.ArticleNotFoundException;
import com.sc7258.realworldjava.exception.CommentNotFoundException;
import com.sc7258.realworldjava.exception.ForbiddenException;
import com.sc7258.realworldjava.users.ProfileService;
import com.sc7258.realworldjava.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final ProfileService profileService;

    @Transactional
    public com.sc7258.realworldjava.model.Comment addComment(String slug, com.sc7258.realworldjava.model.NewComment newComment, User currentUser) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));

        Comment comment = new Comment(newComment.getBody(), currentUser, article);
        Comment savedComment = commentRepository.save(comment);

        return mapToCommentDto(savedComment, currentUser);
    }

    @Transactional(readOnly = true)
    public List<com.sc7258.realworldjava.model.Comment> getCommentsBySlug(String slug, User currentUser) {
        if (!articleRepository.existsBySlug(slug)) {
            throw new ArticleNotFoundException(slug);
        }
        List<Comment> comments = commentRepository.findByArticleSlug(slug);
        return comments.stream()
                .map(comment -> mapToCommentDto(comment, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(String slug, Long id, User currentUser) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        if (!comment.getArticle().equals(article)) {
            throw new CommentNotFoundException(id); // Comment does not belong to this article
        }

        if (!comment.getAuthor().equals(currentUser)) {
            throw new ForbiddenException("You are not the author of this comment.");
        }

        commentRepository.delete(comment);
    }

    private com.sc7258.realworldjava.model.Comment mapToCommentDto(Comment comment, User currentUser) {
        com.sc7258.realworldjava.model.Comment commentDto = new com.sc7258.realworldjava.model.Comment();
        commentDto.setId(comment.getId().intValue());
        commentDto.setBody(comment.getBody());
        commentDto.setCreatedAt(OffsetDateTime.ofInstant(comment.getCreatedAt(), ZoneOffset.UTC));
        commentDto.setUpdatedAt(OffsetDateTime.ofInstant(comment.getUpdatedAt(), ZoneOffset.UTC));
        
        // ProfileService를 사용하여 author 프로필 생성. 현재 사용자가 팔로우하는지 여부를 전달.
        boolean following = currentUser != null && profileService.isFollowing(currentUser, comment.getAuthor());
        commentDto.setAuthor(profileService.buildProfileResponse(comment.getAuthor(), following).getProfile());
        
        return commentDto;
    }
}
