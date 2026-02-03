package com.sc7258.realworldjava.comments;

import com.sc7258.realworldjava.api.CommentsApi;
import com.sc7258.realworldjava.model.MultipleCommentsResponse;
import com.sc7258.realworldjava.model.NewCommentRequest;
import com.sc7258.realworldjava.model.SingleCommentResponse;
import com.sc7258.realworldjava.users.UserRepository;
import com.sc7258.realworldjava.users.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentsController implements CommentsApi {

    private final CommentService commentService;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<SingleCommentResponse> createArticleComment(String slug, @Valid NewCommentRequest comment) {
        User currentUser = getCurrentUserFromSecurityContext();
        com.sc7258.realworldjava.model.Comment commentDto = commentService.addComment(slug, comment.getComment(), currentUser);
        SingleCommentResponse response = new SingleCommentResponse().comment(commentDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MultipleCommentsResponse> getArticleComments(String slug) {
        User currentUser = getCurrentUserFromSecurityContext();
        List<com.sc7258.realworldjava.model.Comment> comments = commentService.getCommentsBySlug(slug, currentUser);
        MultipleCommentsResponse response = new MultipleCommentsResponse().comments(comments);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteArticleComment(String slug, Integer id) {
        User currentUser = getCurrentUserFromSecurityContext();
        // The service expects a Long, so we convert it.
        commentService.deleteComment(slug, id.longValue(), currentUser);
        return ResponseEntity.ok().build();
    }

    private User getCurrentUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return userRepository.findByUsername(principal.getUsername()).orElse(null);
    }
}
