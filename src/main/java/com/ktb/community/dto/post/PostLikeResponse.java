package com.ktb.community.dto.post;

import com.ktb.community.service.PostService;

public record PostLikeResponse(Long postId, boolean liked, long likeCount) {

    public static PostLikeResponse from(PostService.PostLikeResult result) {
        return new PostLikeResponse(result.postId(), result.liked(), result.likeCount());
    }
}
