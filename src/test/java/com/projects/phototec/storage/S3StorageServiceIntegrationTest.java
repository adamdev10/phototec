package com.projects.phototec.storage;

import com.projects.phototec.config.S3Properties;
import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.model.Photo;
import com.projects.phototec.service.StorageService;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;


import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class S3StorageServiceIntegrationTest {

    @Autowired
    private StorageService storageService;

    @Autowired
    private S3Properties properties;

    @Autowired
    private S3Client s3Client;

    private final List<String> uploadedKeys = new ArrayList<>();

    @AfterEach
    void cleanup() {
        for (String key: uploadedKeys){
            try {
                storageService.delete(key);
            } catch (Exception e){}
        }
        uploadedKeys.clear();
    }


    @Test
    void uploadFile() {

        byte[] original = "bytes de prueba".getBytes();

        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", original);

        String key = storageService.upload(file);
        uploadedKeys.add(key);

        byte[] downloaded = storageService.download(key);

        assertArrayEquals(original, downloaded);
    }


    @Test
    void downloadNonexistentFile() {

        String key = "123-123-123";

        assertThrowsExactly(ResourceNotFoundException.class, () -> storageService.download(key));
    }

    @Test
    void deleteFile() {
        byte[] original = "bytes de prueba".getBytes();

        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", original);

        String key = storageService.upload(file);
        uploadedKeys.add(key);

        storageService.delete(key);

        assertThrowsExactly(ResourceNotFoundException.class, () -> storageService.download(key));
    }

}
