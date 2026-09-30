package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.response.FileResponse;
import com.enigmacamp.enigmaCademy.entity.FileStorage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    FileStorage store(MultipartFile file, String category, String referenceId);
    Resource loadFileAsResource(Long id);
    FileResponse getFileById(Long id);
    void deleteFileById(Long id);
}
