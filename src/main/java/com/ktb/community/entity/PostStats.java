package com.ktb.community.entity;

import jakarta.persistence.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "post_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStats {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_post_stats_post"))
    private Post post;

    @Column(nullable = false)
    private Long likeCount = 0L;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long replyCount = 0L;

    public PostStats(Post post) {
        this.post = post;
        this.likeCount = 0L;
        this.viewCount = 0L;
        this.replyCount = 0L;
    }

    // 나중에 redis로 전환
    public void plusLike() {
        this.likeCount++;
    }
    public void plusView() {
        this.viewCount++;
    }
    public void plusReply() {
        this.replyCount++;
    }
}


