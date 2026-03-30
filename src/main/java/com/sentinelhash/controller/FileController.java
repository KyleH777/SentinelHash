package com.sentinelhash.controller;

import com.sentinelhash.dto.FileUploadResponse;
import com.sentinelhash.dto.FileVerifyResponse;
import com.sentinelhash.model.FileRecord;
import com.sentinelhash.service.IntegrityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final IntegrityService integrityService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(
            @RequestParam("file") MultipartFile file) throws IOException, NoSuchAlgorithmException {

        FileRecord record = integrityService.saveFile(file);

        FileUploadResponse response = FileUploadResponse.builder()
                .id(record.getId())
                .originalFilename(record.getOriginalFilename())
                .hashValue(record.getHashValue())
                .algorithm(record.getAlgorithm())
                .fileSize(record.getFileSize())
                .uploadedAt(record.getUploadedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileVerifyResponse> verify(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") Long id) throws IOException, NoSuchAlgorithmException {

        return ResponseEntity.ok(integrityService.verifyFile(file, id));
    }
}
