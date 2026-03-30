package com.sentinelhash.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class IntegrityMismatchedException extends RuntimeException {

    private final String computedHash;
    private final String expectedHash;

    public IntegrityMismatchedException(String computedHash, String expectedHash) {
        super("Integrity check failed: hash mismatch detected");
        this.computedHash = computedHash;
        this.expectedHash = expectedHash;
    }
}
