package com.ktb.community.repository;

import com.ktb.community.entity.QComment;
import com.ktb.community.entity.QPost;
import com.ktb.community.entity.QUser;
import com.ktb.community.repository.projection.PostSummaryProjection;
import com.ktb.community.support.CursorPage;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPage<PostSummaryProjection> findAllByCursor(Long cursorId, int size) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QComment comment = QComment.comment;

        List<PostSummaryProjection> results = queryFactory
                .select(Projections.constructor(
                        PostSummaryProjection.class,
                        post.id,
                        post.title,
                        post.content,
                        user.id,
                        user.nickname,
                        post.createdAt,
                        post.updatedAt,
                        comment.id.count()
                ))
                .from(post)
                .join(post.user, user)
                .leftJoin(post.comments, comment)
                .on(comment.deletedAt.isNull())
                .where(post.deletedAt.isNull(), cursorPredicate(cursorId))
                .groupBy(post.id, user.id, user.nickname, post.title, post.content, post.createdAt, post.updatedAt)
                .orderBy(post.id.desc())
                .limit(size + 1L)
                .fetch();

        boolean hasNext = results.size() > size;
        Long nextCursor = null;
        if (hasNext) {
            PostSummaryProjection last = results.remove(size);
            nextCursor = last.id();
        } else if (!results.isEmpty()) {
            nextCursor = results.get(results.size() - 1).id();
        }

        return new CursorPage<>(results, nextCursor, hasNext);
    }

    private BooleanExpression cursorPredicate(Long cursorId) {
        if (cursorId == null) {
            return null;
        }
        return QPost.post.id.lt(cursorId);
    }
}
