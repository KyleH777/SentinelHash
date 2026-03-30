package com.sentinelhash.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponse {

    private Long id;
    private String originalFilename;
    private String hashValue;
    private String algorithm;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}
