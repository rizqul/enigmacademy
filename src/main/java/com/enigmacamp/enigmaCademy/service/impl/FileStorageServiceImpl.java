package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.entity.FileStorage;
import com.enigmacamp.enigmaCademy.repository.FileStorageRepository;
import com.enigmacamp.enigmaCademy.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    private FileStorageRepository fileStorageRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private Path fileStorageLocation;


    // init folder dulu
    @PostConstruct
    public void init() throws IOException {
        try {
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

            Files.createDirectory(this.fileStorageLocation);
            log.info("Direktory sudah berhasil dibuat di " + this.fileStorageLocation);

        } catch (IOException e) {
            throw new RuntimeException("Gagal dalam proses pembuatan direktori : " + e.getMessage());
        }
    }

    @Override
    public FileStorage store(MultipartFile file, String category, String referenceId) {

        return null;
    }
}
