package com.ktb.community.dto.post;

import com.ktb.community.entity.File;
import com.ktb.community.entity.Post;
import com.ktb.community.dto.user.UserResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        UserResponse author,
        List<String> fileUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PostResponse from(Post post) {
        List<String> fileUrls = post.getFiles().stream()
                .map(File::getFileUrl)
                .toList();
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                UserResponse.from(post.getUser()),
                fileUrls,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
