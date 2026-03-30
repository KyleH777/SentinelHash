package com.sentinelhash.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileVerifyResponse {

    private Long recordId;
    private String originalFilename;
    private String computedHash;
    private String storedHash;
    private boolean match;
}
