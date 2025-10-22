package com.ktb.community.service;

import com.ktb.community.entity.Comment;
import com.ktb.community.entity.Post;
import com.ktb.community.entity.User;
import com.ktb.community.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;
    @Mock
    private OwnershipVerifier ownershipVerifier;
    @Mock
    private PostStatsService postStatsService;

    @InjectMocks
    private CommentService commentService;

    private User author;
    private Post post;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .email("user@example.com")
                .password("pw")
                .nickname("writer")
                .active(true)
                .admin(false)
                .deleted(false)
                .build();
        ReflectionTestUtils.setField(author, "id", 1L);
        post = Post.create(author, "title", "content");
        ReflectionTestUtils.setField(post, "id", 10L);
    }

    @Test
    void addComment_persistsCommentAndIncrementsReplyCount() {
        when(postService.getPostOrThrow(10L)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            ReflectionTestUtils.setField(comment, "id", 100L);
            return comment;
        });

        Comment saved = commentService.addComment(10L, author, "nice post");

        assertThat(saved.getId()).isEqualTo(100L);
        assertThat(saved.getPost()).isSameAs(post);
        assertThat(post.getComments()).contains(saved);
        verify(postStatsService).increaseReply(10L);
    }

    @Test
    void getComments_returnsPageFromRepository() {
        when(postService.getPostOrThrow(10L)).thenReturn(post);
        PageRequest pageable = PageRequest.of(0, 20);
        Comment comment = Comment.create(author, post, "content");
        ReflectionTestUtils.setField(comment, "id", 1L);
        Page<Comment> page = new PageImpl<>(List.of(comment), pageable, 1);
        when(commentRepository.findPageByPostId(10L, pageable)).thenReturn(page);

        Page<Comment> result = commentService.getComments(10L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(1L);
    }

    @Test
    void updateComment_updatesContentAfterOwnershipCheck() {
        Comment comment = Comment.create(author, post, "old");
        ReflectionTestUtils.setField(comment, "id", 5L);
        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        Comment updated = commentService.updateComment(5L, author, "new content");

        assertThat(updated.getContent()).isEqualTo("new content");
        verify(ownershipVerifier).check(comment, author, "Only author can modify this comment");
    }

    @Test
    void updateComment_whenDeleted_throwsNotFound() {
        Comment comment = Comment.create(author, post, "content");
        comment.softDelete();
        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment(5L, author, "new"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteComment_softDeletesAndUpdatesStats() {
        Comment comment = Comment.create(author, post, "content");
        ReflectionTestUtils.setField(comment, "id", 5L);
        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(5L, author);

        assertThat(comment.isDeleted()).isTrue();
        verify(ownershipVerifier).check(comment, author, "Only author can modify this comment");
        verify(postStatsService).decreaseReply(10L);
    }

    @Test
    void deleteComment_whenNotFound_throws() {
        when(commentRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(5L, author))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
