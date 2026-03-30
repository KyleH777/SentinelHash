package com.sentinelhash.controller;

import com.sentinelhash.dto.FileVerifyResponse;
import com.sentinelhash.exception.IntegrityMismatchedException;
import com.sentinelhash.model.FileRecord;
import com.sentinelhash.service.IntegrityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IntegrityService integrityService;

    private MockMultipartFile validFile;
    private FileRecord savedRecord;
    private static final String STORED_HASH = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";

    @BeforeEach
    void setUp() {
        validFile = new MockMultipartFile(
                "file",
                "document.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello".getBytes()
        );

        savedRecord = FileRecord.builder()
                .id(1L)
                .originalFilename("document.txt")
                .storedFilename("uuid_document.txt")
                .filePath("/uploads/uuid_document.txt")
                .hashValue(STORED_HASH)
                .algorithm("SHA-256")
                .fileSize(5L)
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "sentinel", roles = "USER")
    void upload_shouldReturn201_withHashResponse() throws Exception {
        when(integrityService.saveFile(any())).thenReturn(savedRecord);

        mockMvc.perform(multipart("/api/files/upload")
                        .file(validFile)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.originalFilename").value("document.txt"))
                .andExpect(jsonPath("$.hashValue").value(STORED_HASH))
                .andExpect(jsonPath("$.algorithm").value("SHA-256"));
    }

    @Test
    void upload_shouldReturn401_whenUnauthenticated() throws Exception {
        mockMvc.perform(multipart("/api/files/upload")
                        .file(validFile)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "sentinel", roles = "USER")
    void verify_shouldReturn200_whenFileIsIntact() throws Exception {
        FileVerifyResponse verifyResponse = FileVerifyResponse.builder()
                .recordId(1L)
                .originalFilename("document.txt")
                .computedHash(STORED_HASH)
                .storedHash(STORED_HASH)
                .match(true)
                .build();

        when(integrityService.verifyFile(any(), eq(1L))).thenReturn(verifyResponse);

        mockMvc.perform(multipart("/api/files/verify")
                        .file(validFile)
                        .param("id", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.match").value(true))
                .andExpect(jsonPath("$.computedHash").value(STORED_HASH))
                .andExpect(jsonPath("$.storedHash").value(STORED_HASH));
    }

    @Test
    @WithMockUser(username = "sentinel", roles = "USER")
    void verify_shouldReturn409_whenFileIsTampered() throws Exception {
        String tamperedHash = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        when(integrityService.verifyFile(any(), eq(1L)))
                .thenThrow(new IntegrityMismatchedException(tamperedHash, STORED_HASH));

        mockMvc.perform(multipart("/api/files/verify")
                        .file(validFile)
                        .param("id", "1")
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.computedHash").value(tamperedHash))
                .andExpect(jsonPath("$.expectedHash").value(STORED_HASH))
                .andExpect(jsonPath("$.message").value("Integrity check failed: hash mismatch detected"));
    }

    @Test
    @WithMockUser(username = "sentinel", roles = "USER")
    void verify_shouldBePubliclyAccessible_withoutAuth() throws Exception {
        FileVerifyResponse verifyResponse = FileVerifyResponse.builder()
                .recordId(1L)
                .originalFilename("document.txt")
                .computedHash(STORED_HASH)
                .storedHash(STORED_HASH)
                .match(true)
                .build();

        when(integrityService.verifyFile(any(), eq(1L))).thenReturn(verifyResponse);

        mockMvc.perform(multipart("/api/files/verify")
                        .file(validFile)
                        .param("id", "1")
                        .with(csrf())
                        .with(anonymous()))
                .andExpect(status().isOk());
    }
}
