package com.ktb.community.service;

import com.ktb.community.entity.Comment;
import com.ktb.community.entity.Post;
import com.ktb.community.entity.User;
import com.ktb.community.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public Comment addComment(Long postId, User user, String content) {
        Post post = postService.getPostOrThrow(postId);
        Comment comment = Comment.create(user, post, content);
        return commentRepository.save(comment);
    }

    public Page<Comment> getComments(Long postId, Pageable pageable) {
        postService.getPostOrThrow(postId);
        return commentRepository.findPageByPostId(postId, pageable);
    }

    @Transactional
    public Comment updateComment(Long commentId, User user, String content) {
        Comment comment = getActiveComment(commentId);
        verifyOwnership(comment, user);
        comment.updateContent(content);
        return comment;
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = getActiveComment(commentId);
        verifyOwnership(comment, user);
        comment.softDelete();
    }

    private Comment getActiveComment(Long commentId) {
        return commentRepository.findById(commentId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
    }

    private void verifyOwnership(Comment comment, User user) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can modify this comment");
        }
    }
}
