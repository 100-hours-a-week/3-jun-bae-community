package com.ktb.community.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

//복합키 설정
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class PostFileId implements Serializable {
    private Long post;
    private Long file;
}

@Entity
@Table(name = "posts_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PostFileId.class)
public class PostFile {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_pf_post"))
    private Post post;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", foreignKey = @ForeignKey(name = "fk_pf_file"))
    private File file;

    PostFile(Post post, File file) {
        this.post = post;
        this.file = file;
    }
}



