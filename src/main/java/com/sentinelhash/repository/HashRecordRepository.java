package com.sentinelhash.repository;

import com.sentinelhash.model.HashRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashRecordRepository extends JpaRepository<HashRecord, Long> {

    Optional<HashRecord> findByHashValue(String hashValue);

    List<HashRecord> findByOriginalData(String originalData);

    boolean existsByHashValue(String hashValue);
}
