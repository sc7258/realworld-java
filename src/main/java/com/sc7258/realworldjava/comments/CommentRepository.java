package com.sc7258.realworldjava.comments;

import com.sc7258.realworldjava.comments.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleSlug(String slug);
}
