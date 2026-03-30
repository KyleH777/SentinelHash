package com.sentinelhash.service;

import com.sentinelhash.dto.HashRequest;
import com.sentinelhash.dto.HashResponse;
import com.sentinelhash.exception.ResourceNotFoundException;
import com.sentinelhash.model.HashRecord;
import com.sentinelhash.repository.HashRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashService {

    private final HashRecordRepository hashRecordRepository;

    @Transactional
    public HashResponse generateHash(HashRequest request) {
        String hashValue = computeSHA256(request.getData());
        HashRecord record = HashRecord.builder()
                .originalData(request.getData())
                .hashValue(hashValue)
                .build();
        return mapToResponse(hashRecordRepository.save(record));
    }

    public boolean verifyIntegrity(String data, String expectedHash) {
        return computeSHA256(data).equalsIgnoreCase(expectedHash);
    }

    public HashResponse getById(Long id) {
        return hashRecordRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("HashRecord not found with id: " + id));
    }

    public List<HashResponse> getAll() {
        return hashRecordRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) {
        if (!hashRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("HashRecord not found with id: " + id);
        }
        hashRecordRepository.deleteById(id);
    }

    private String computeSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private HashResponse mapToResponse(HashRecord record) {
        return HashResponse.builder()
                .id(record.getId())
                .originalData(record.getOriginalData())
                .hashValue(record.getHashValue())
                .algorithm(record.getAlgorithm())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
