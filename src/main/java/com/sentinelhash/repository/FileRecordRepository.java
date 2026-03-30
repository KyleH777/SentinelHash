package com.sentinelhash.repository;

import com.sentinelhash.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {

    Optional<FileRecord> findByHashValue(String hashValue);

    List<FileRecord> findByOriginalFilename(String originalFilename);
}
