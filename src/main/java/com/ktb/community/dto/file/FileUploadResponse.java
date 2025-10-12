package com.ktb.community.dto.file;

import com.ktb.community.entity.File;

public record FileUploadResponse(
        Long id,
        String fileName,
        String url
) {

    public static FileUploadResponse from(File file) {
        return new FileUploadResponse(file.getId(), file.getOriginalFileName(), file.getFileUrl());
    }
}
