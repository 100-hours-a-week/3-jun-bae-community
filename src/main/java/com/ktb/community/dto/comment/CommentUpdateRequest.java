package com.ktb.community.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(@NotBlank String content) {
}
