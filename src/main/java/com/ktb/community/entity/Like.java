package com.ktb.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_likes_user_post",
                columnNames = {"user_id", "post_id"}
        )
)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @ManyToOne
    public Post post;

    @CreatedDate
    public LocalDateTime createdAt;

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
