package com.ktb.community.repository.projection;

import java.time.LocalDateTime;

public record PostSummaryProjection(
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
}
