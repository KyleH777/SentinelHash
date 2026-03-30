package com.sentinelhash.controller;

import com.sentinelhash.dto.HashRequest;
import com.sentinelhash.dto.HashResponse;
import com.sentinelhash.dto.VerifyRequest;
import com.sentinelhash.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/hashes")
@RequiredArgsConstructor
public class HashController {

    private final HashService hashService;

    @PostMapping
    public ResponseEntity<HashResponse> generate(@RequestBody HashRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hashService.generateHash(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verify(@RequestBody VerifyRequest request) {
        boolean valid = hashService.verifyIntegrity(request.getData(), request.getExpectedHash());
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @GetMapping
    public ResponseEntity<List<HashResponse>> getAll() {
        return ResponseEntity.ok(hashService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HashResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hashService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        hashService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
