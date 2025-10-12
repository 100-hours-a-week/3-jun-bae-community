package com.ktb.community.dto.comment;

import org.springframework.data.domain.Page;

import java.util.List;

public record CommentPageResponse(
        List<CommentResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static CommentPageResponse from(Page<CommentResponse> page) {
        return new CommentPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
