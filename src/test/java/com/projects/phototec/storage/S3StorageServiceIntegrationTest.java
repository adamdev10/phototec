package com.projects.phototec.storage;

import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.service.StorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;


import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class S3StorageServiceIntegrationTest {

    @Autowired
    private StorageService storageService;

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

        byte[] original = "test bytes".getBytes();

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
        byte[] original = "test bytes".getBytes();

        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", original);

        String key = storageService.upload(file);
        uploadedKeys.add(key);

        storageService.delete(key);

        assertThrowsExactly(ResourceNotFoundException.class, () -> storageService.download(key));
    }

}
