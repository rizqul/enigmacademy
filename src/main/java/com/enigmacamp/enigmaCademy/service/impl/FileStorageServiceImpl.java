package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.entity.FileStorage;
import com.enigmacamp.enigmaCademy.repository.FileStorageRepository;
import com.enigmacamp.enigmaCademy.service.FileStorageService;
import com.enigmacamp.enigmaCademy.utils.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    @Transactional
    public FileStorage store(MultipartFile file, String category, String referenceId) {
        // validasi File
        validateFile(file);

        // get original fileName
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Generate nama file secara unik
        String fileExtension = getFileExtension(originalFilename);
        String newFileName = UUID.randomUUID() + "." + fileExtension;

        // proses penyimpanan metadata didalam database
        try {
            // validasi nama file
            if(originalFilename.contains("..")){
                throw new BadRequestException("Nama file tidak valid : " + originalFilename);
            }

            // Pemindahan file ke lokasi penyimpanan
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Buat entity FileStorage dan simpan ke dalam database
            FileStorage fileStorage = FileStorage.builder()
                    .name(originalFilename)
                    .fileName(newFileName)
                    .filePath(targetLocation.toString())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .category(category)
                    .build();

            return fileStorageRepository.save(fileStorage);

        } catch (Exception e) {
            throw new RuntimeException("gagal menyimpan file : " + originalFilename + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void validateFile(MultipartFile file) {
        // validasi apakah file kosong
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File tidak boleh kosong!");
        }

        // validasi tipe file -> apa saja tipe file yang diizinkan untuk disimpan
        String contentType = file.getContentType();
        List<String> allowedTypes = Arrays.asList(
                "image/jpeg", "image/jpg", "image/png", "image/gif", "application/pdf", "application/msword"
        );

        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new BadRequestException("Tipe file tidak didukung. Hanya mendukung file bertipe JPEG, JPG, GIF, PNG, PDF, DOC");
        }

        // validasi ukuran
        if(file.getSize() > 10 * 1024 * 1024) {// 10 MB
            throw new BadRequestException("Ukuran file terlalu besar. Maksimal ukuran file 10MB");
        }
    }
}
