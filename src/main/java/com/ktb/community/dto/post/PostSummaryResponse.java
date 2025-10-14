package com.ktb.community.dto.post;

import com.ktb.community.repository.projection.PostSummaryProjection;

import java.time.LocalDateTime;

public record PostSummaryResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorNickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long viewCount,
        Long likeCount,
        Long replyCount
) {

    public static PostSummaryResponse from(PostSummaryProjection projection) {
        return new PostSummaryResponse(
                projection.id(),
                projection.title(),
                projection.content(),
                projection.authorId(),
                projection.authorNickname(),
                projection.createdAt(),
                projection.updatedAt(),
                projection.viewCount(),
                projection.likeCount(),
                projection.replyCount()
        );
    }
}
