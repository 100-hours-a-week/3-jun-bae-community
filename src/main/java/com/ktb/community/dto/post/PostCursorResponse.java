package com.ktb.community.dto.post;

import com.ktb.community.support.CursorPage;

import java.util.List;

public record PostCursorResponse(
        List<PostSummaryResponse> items,
        Long nextCursor,
        boolean hasNext
) {

    public static PostCursorResponse from(CursorPage<PostSummaryResponse> page) {
        return new PostCursorResponse(page.getContents(), page.getNextCursor(), page.isHasNext());
    }
}
