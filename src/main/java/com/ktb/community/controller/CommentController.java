package com.ktb.community.controller;

import com.ktb.community.dto.comment.CommentCreateRequest;
import com.ktb.community.dto.comment.CommentPageResponse;
import com.ktb.community.dto.comment.CommentResponse;
import com.ktb.community.dto.comment.CommentUpdateRequest;
import com.ktb.community.entity.Comment;
import com.ktb.community.entity.User;
import com.ktb.community.service.CommentService;
import com.ktb.community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentPageResponse> list(@PathVariable Long postId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CommentResponse> commentPage = commentService.getComments(postId, pageable)
                .map(CommentResponse::from);
        return ResponseEntity.ok(CommentPageResponse.from(commentPage));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> create(@PathVariable Long postId,
                                                  @Valid @RequestBody CommentCreateRequest request,
                                                  @AuthenticationPrincipal UserDetails principal) {
        ensureAuthenticated(principal);
        User user = userService.getByEmailOrThrow(principal.getUsername());
        Comment comment = commentService.addComment(postId, user, request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(comment));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> update(@PathVariable Long commentId,
                                                  @Valid @RequestBody CommentUpdateRequest request,
                                                  @AuthenticationPrincipal UserDetails principal) {
        ensureAuthenticated(principal);
        User user = userService.getByEmailOrThrow(principal.getUsername());
        Comment updated = commentService.updateComment(commentId, user, request.content());
        return ResponseEntity.ok(CommentResponse.from(updated));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId,
                                       @AuthenticationPrincipal UserDetails principal) {
        ensureAuthenticated(principal);
        User user = userService.getByEmailOrThrow(principal.getUsername());
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }

    private void ensureAuthenticated(UserDetails principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
    }
}
