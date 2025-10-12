package com.ktb.community.dto.auth;

import com.ktb.community.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record AuthResponse(
        Long userId,
        String email,
        String nickname,
        List<String> roles,
        LocalDateTime lastLoginAt
) {

    public static AuthResponse from(User user, List<String> roles) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                roles,
                user.getLastLoginAt()
        );
    }
}
