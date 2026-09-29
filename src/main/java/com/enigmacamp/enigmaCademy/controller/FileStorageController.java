package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.entity.FileStorage;
import com.enigmacamp.enigmaCademy.service.FileStorageService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/files")
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
}
