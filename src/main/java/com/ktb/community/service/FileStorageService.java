package com.ktb.community.service;

import com.ktb.community.entity.File;
import com.ktb.community.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileStorageService {

    private final FileRepository fileRepository;

    @Transactional
    public File upload(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File payload is empty");
        }
        String originalFilename = multipartFile.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            originalFilename = "anonymous";
        }

        String storageKey = "uploads/" + UUID.randomUUID();
        String fileUrl = "https://s3.dummy.local/" + storageKey;

        // read stream to ensure payload is accessible; discard result for dummy implementation
        try {
            multipartFile.getInputStream().transferTo(java.io.OutputStream.nullOutputStream());
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read file content", ex);
        }

        File file = File.pending(originalFilename, storageKey, fileUrl);
        return fileRepository.save(file);
    }

    public File getOrThrow(Long fileId) {
        return fileRepository.findByIdAndDeletedAtIsNull(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
    }
}
