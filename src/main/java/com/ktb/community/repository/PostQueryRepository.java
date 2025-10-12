package com.ktb.community.repository;

import com.ktb.community.repository.projection.PostSummaryProjection;
import com.ktb.community.support.CursorPage;

public interface PostQueryRepository {

    CursorPage<PostSummaryProjection> findAllByCursor(Long cursorId, int size);
}
