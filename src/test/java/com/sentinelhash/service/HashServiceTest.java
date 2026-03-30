package com.sentinelhash.service;

import com.sentinelhash.dto.HashRequest;
import com.sentinelhash.dto.HashResponse;
import com.sentinelhash.exception.ResourceNotFoundException;
import com.sentinelhash.model.HashRecord;
import com.sentinelhash.repository.HashRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {

    @Mock
    private HashRecordRepository hashRecordRepository;

    @InjectMocks
    private HashService hashService;

    private HashRecord mockRecord;

    @BeforeEach
    void setUp() {
        mockRecord = HashRecord.builder()
                .id(1L)
                .originalData("hello")
                .hashValue("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824")
                .algorithm("SHA-256")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void generateHash_shouldReturnHashResponse() {
        when(hashRecordRepository.save(any(HashRecord.class))).thenReturn(mockRecord);

        HashResponse response = hashService.generateHash(new HashRequest("hello"));

        assertThat(response).isNotNull();
        assertThat(response.getHashValue()).isEqualTo("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824");
        assertThat(response.getAlgorithm()).isEqualTo("SHA-256");
    }

    @Test
    void verifyIntegrity_shouldReturnTrue_whenHashMatches() {
        boolean result = hashService.verifyIntegrity(
                "hello",
                "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
        );
        assertThat(result).isTrue();
    }

    @Test
    void verifyIntegrity_shouldReturnFalse_whenHashDoesNotMatch() {
        assertThat(hashService.verifyIntegrity("hello", "wronghash")).isFalse();
    }

    @Test
    void getById_shouldReturnHashResponse_whenRecordExists() {
        when(hashRecordRepository.findById(1L)).thenReturn(Optional.of(mockRecord));

        HashResponse response = hashService.getById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOriginalData()).isEqualTo("hello");
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenNotFound() {
        when(hashRecordRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hashService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAll_shouldReturnAllRecords() {
        when(hashRecordRepository.findAll()).thenReturn(List.of(mockRecord));

        List<HashResponse> results = hashService.getAll();

        assertThat(results).hasSize(1);
    }

    @Test
    void deleteById_shouldDelete_whenRecordExists() {
        when(hashRecordRepository.existsById(1L)).thenReturn(true);

        hashService.deleteById(1L);

        verify(hashRecordRepository).deleteById(1L);
    }

    @Test
    void deleteById_shouldThrow_whenRecordNotFound() {
        when(hashRecordRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> hashService.deleteById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
