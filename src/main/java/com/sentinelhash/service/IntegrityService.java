package com.sentinelhash.service;

import com.sentinelhash.dto.FileVerifyResponse;
import com.sentinelhash.exception.IntegrityMismatchedException;
import com.sentinelhash.exception.ResourceNotFoundException;
import com.sentinelhash.model.FileRecord;
import com.sentinelhash.repository.FileRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntegrityService {

    private final FileRecordRepository fileRecordRepository;

    @Value("${app.upload.dir:/uploads}")
    private String uploadDirectory;

    public String computeHash(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    @Transactional
    public FileRecord saveFile(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        Path uploadDir = Paths.get(uploadDirectory);
        Files.createDirectories(uploadDir);

        String storedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destination = uploadDir.resolve(storedFilename);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (DigestInputStream dis = new DigestInputStream(file.getInputStream(), digest)) {
            Files.copy(dis, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        String hashValue = HexFormat.of().formatHex(digest.digest());

        FileRecord record = FileRecord.builder()
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedFilename)
                .filePath(destination.toAbsolutePath().toString())
                .hashValue(hashValue)
                .fileSize(file.getSize())
                .build();

        return fileRecordRepository.save(record);
    }

    public FileVerifyResponse verifyFile(MultipartFile file, Long id) throws IOException, NoSuchAlgorithmException {
        FileRecord record = fileRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FileRecord not found with id: " + id));

        String computedHash = computeHash(file);

        if (!computedHash.equalsIgnoreCase(record.getHashValue())) {
            throw new IntegrityMismatchedException(computedHash, record.getHashValue());
        }

        return FileVerifyResponse.builder()
                .recordId(record.getId())
                .originalFilename(record.getOriginalFilename())
                .computedHash(computedHash)
                .storedHash(record.getHashValue())
                .match(true)
                .build();
    }
}
