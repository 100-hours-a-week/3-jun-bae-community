package com.ktb.community.repository;

import com.ktb.community.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {

    Optional<Post> findByIdAndDeletedAtIsNull(Long id);

    @EntityGraph(attributePaths = {"user", "attachments", "attachments.file"})
    Optional<Post> findWithFilesByIdAndDeletedAtIsNull(Long id);
}
