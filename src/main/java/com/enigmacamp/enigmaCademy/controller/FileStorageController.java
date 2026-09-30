package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.dto.response.FileResponse;
import com.enigmacamp.enigmaCademy.entity.FileStorage;
import com.enigmacamp.enigmaCademy.service.FileStorageService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileStorageController {
    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<FileStorage>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") @NotBlank String category,
            @RequestParam(value = "referenceId", required = false) String referenceId
            ) {

        FileStorage savedFile = fileStorageService.store(file, category, referenceId);

        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                ResponseMessage.SUCCES_SAVE_DATA,
                savedFile
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        // load resource
        Resource resource = fileStorageService.loadFileAsResource(id);

        // sediakan file info
        FileResponse fileInfo = fileStorageService.getFileById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<String>> deleteFile(@PathVariable Long id) {
        fileStorageService.deleteFileById(id);

        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ResponseMessage.SUCCES_DELETE_DATA,
                "File berhasil dihapus"
        );
    }
}
