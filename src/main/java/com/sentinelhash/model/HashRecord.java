package com.sentinelhash.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hash_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalData;

    @Column(nullable = false, length = 64)
    private String hashValue;

    @Column(nullable = false)
    private String algorithm;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        algorithm = "SHA-256";
    }
}
