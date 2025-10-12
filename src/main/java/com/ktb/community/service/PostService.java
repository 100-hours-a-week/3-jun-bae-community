package com.ktb.community.service;

import com.ktb.community.entity.File;
import com.ktb.community.entity.Post;
import com.ktb.community.entity.User;
import com.ktb.community.dto.post.PostSummaryResponse;
import com.ktb.community.repository.FileRepository;
import com.ktb.community.repository.PostRepository;
import com.ktb.community.repository.projection.PostSummaryProjection;
import com.ktb.community.support.CursorPage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final FileRepository fileRepository;

    @Transactional
    public Post createPost(User author, String title, String content, List<Long> fileIds) {
        Post post = Post.create(author, title, content);
        attachFiles(post, fileIds);
        return postRepository.save(post);
    }

    public CursorPage<PostSummaryResponse> getPosts(Long cursorId, int size) {
        CursorPage<PostSummaryProjection> page = postRepository.findAllByCursor(cursorId, size);
        List<PostSummaryResponse> responses = page.getContents().stream()
                .map(PostSummaryResponse::from)
                .toList();
        return new CursorPage<>(responses, page.getNextCursor(), page.isHasNext());
    }

    public Post getPostOrThrow(Long postId) {
        return postRepository.findWithFilesByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    @Transactional
    public Post updatePost(Long postId, User user, String title, String content, List<Long> fileIds) {
        Post post = getPostOrThrow(postId);
        verifyOwnership(post, user);
        post.update(title, content);
        if (fileIds != null) {
            List<File> files = loadFiles(fileIds);
            post.replaceAttachments(files);
            files.forEach(File::markCommitted);
        }
        return post;
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = getPostOrThrow(postId);
        verifyOwnership(post, user);
        post.softDelete();
    }

    private void attachFiles(Post post, List<Long> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return;
        }
        List<File> files = loadFiles(fileIds);
        post.addAttachments(files);
        files.forEach(File::markCommitted);
    }

    private List<File> loadFiles(List<Long> fileIds) {
        List<File> files = fileRepository.findByIdIn(fileIds);
        if (files.size() != fileIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more files not found");
        }
        return files;
    }

    private void verifyOwnership(Post post, User user) {
        if (!post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can modify this post");
        }
    }
}
