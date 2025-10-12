package com.ktb.community.dto.user;

import com.ktb.community.entity.File;
import com.ktb.community.entity.User;

public record UserResponse(
        Long id,
        String email,
        String nickname,
        String profileImageUrl
) {

    public static UserResponse from(User user) {
        File profileImage = user.getProfileImage();
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                profileImage != null ? profileImage.getFileUrl() : null
        );
    }
}
