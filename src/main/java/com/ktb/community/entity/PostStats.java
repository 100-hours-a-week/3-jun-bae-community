package com.ktb.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStats {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_post_stats_post"))
    private Post post;

    @Column(nullable = false)
    private long likeCount;

    @Column(nullable = false)
    private long viewCount;

    @Column(nullable = false)
    private long replyCount;

    private PostStats(Post post) {
        this.post = post;
        this.postId = post.getId();
        this.likeCount = 0L;
        this.viewCount = 0L;
        this.replyCount = 0L;
    }

    public static PostStats initialize(Post post) {
        return new PostStats(post);
    }

    void linkPost(Post post) {
        this.post = post;
        this.postId = post.getId();
    }

    public void incrementLike() {
        this.likeCount++;
    }

    public void decrementLike() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementView() {
        this.viewCount++;
    }

    public void incrementReply() {
        this.replyCount++;
    }

    public void decrementReply() {
        if (this.replyCount > 0) {
            this.replyCount--;
        }
    }
}
