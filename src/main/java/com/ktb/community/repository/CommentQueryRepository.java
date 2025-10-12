package com.ktb.community.repository;

import com.ktb.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentQueryRepository {

    Page<Comment> findPageByPostId(Long postId, Pageable pageable);
}
