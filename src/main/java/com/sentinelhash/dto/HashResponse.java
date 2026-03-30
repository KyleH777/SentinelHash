package com.sentinelhash.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashResponse {

    private Long id;
    private String originalData;
    private String hashValue;
    private String algorithm;
    private LocalDateTime createdAt;
}
