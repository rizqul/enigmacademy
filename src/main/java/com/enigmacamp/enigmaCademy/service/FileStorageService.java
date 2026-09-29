package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.entity.FileStorage;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    FileStorage store(MultipartFile file, String category, String referenceId);
}
