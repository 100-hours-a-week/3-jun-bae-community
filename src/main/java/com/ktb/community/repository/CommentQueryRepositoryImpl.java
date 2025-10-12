package com.ktb.community.repository;

import com.ktb.community.entity.QComment;
import com.ktb.community.entity.QUser;
import com.ktb.community.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Comment> findPageByPostId(Long postId, Pageable pageable) {
        QComment comment = QComment.comment;
        QUser user = QUser.user;

        List<Comment> content = queryFactory
                .selectFrom(comment)
                .join(comment.user, user).fetchJoin()
                .where(comment.post.id.eq(postId), comment.deletedAt.isNull())
                .orderBy(comment.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.post.id.eq(postId), comment.deletedAt.isNull())
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
