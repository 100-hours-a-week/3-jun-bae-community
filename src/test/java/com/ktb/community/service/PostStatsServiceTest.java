package com.ktb.community.service;

import com.ktb.community.entity.Post;
import com.ktb.community.entity.PostStats;
import com.ktb.community.entity.User;
import com.ktb.community.repository.PostRepository;
import com.ktb.community.repository.PostStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostStatsServiceTest {

    @Mock
    private PostStatsRepository postStatsRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostStatsService postStatsService;

    private Post post;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("user@example.com")
                .password("pw")
                .nickname("user")
                .active(true)
                .admin(false)
                .deleted(false)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        post = Post.create(user, "title", "content");
        ReflectionTestUtils.setField(post, "id", 10L);
    }

    @Test
    void initialize_whenPostNotSaved_throwsBadRequest() {
        Post unsaved = Post.create(post.getUser(), "t", "c");

        assertThatThrownBy(() -> postStatsService.initialize(unsaved))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void initialize_whenStatsExist_returnsExisting() {
        PostStats existing = PostStats.initialize(post);
        ReflectionTestUtils.setField(existing, "likeCount", 3L);
        when(postStatsRepository.findByPostId(10L)).thenReturn(Optional.of(existing));

        PostStats result = postStatsService.initialize(post);

        assertThat(result).isSameAs(existing);
        verify(postStatsRepository, never()).save(existing);
    }

    @Test
    void initialize_whenStatsMissing_createsAndLinks() {
        when(postStatsRepository.findByPostId(10L)).thenReturn(Optional.empty());
        when(postStatsRepository.save(org.mockito.ArgumentMatchers.any(PostStats.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PostStats created = postStatsService.initialize(post);

        assertThat(created.getPost()).isSameAs(post);
        assertThat(post.getStats()).isSameAs(created);
        verify(postStatsRepository).save(created);
    }

    @Test
    void increaseView_incrementsCounter() {
        PostStats stats = PostStats.initialize(post);
        when(postStatsRepository.findByPostId(10L)).thenReturn(Optional.of(stats));

        PostStats result = postStatsService.increaseView(10L);

        assertThat(result.getViewCount()).isEqualTo(1L);
    }

    @Test
    void decreaseLike_doesNotDropBelowZero() {
        PostStats stats = PostStats.initialize(post);
        when(postStatsRepository.findByPostId(10L)).thenReturn(Optional.of(stats));

        PostStats result = postStatsService.decreaseLike(10L);

        assertThat(result.getLikeCount()).isZero();
    }

    @Test
    void getStats_whenStatsMissing_createsUsingPost() {
        when(postStatsRepository.findByPostId(10L)).thenReturn(Optional.empty());
        when(postRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(post));
        when(postStatsRepository.save(org.mockito.ArgumentMatchers.any(PostStats.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PostStats result = postStatsService.getStats(10L);

        assertThat(result.getPost()).isSameAs(post);
        verify(postStatsRepository).save(result);
    }

    @Test
    void getStats_whenPostMissing_throwsNotFound() {
        when(postStatsRepository.findByPostId(10L)).thenReturn(Optional.empty());
        when(postRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postStatsService.getStats(10L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
