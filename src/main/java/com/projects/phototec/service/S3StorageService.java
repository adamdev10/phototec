package com.projects.phototec.service;

import com.projects.phototec.config.S3Properties;
import com.projects.phototec.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3StorageService implements StorageService{

    private final S3Client s3Client;
    private final S3Properties properties;

    public S3StorageService(
            S3Client s3Client,
            S3Properties properties
    ) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            String key = UUID.randomUUID() + "-" + file.getOriginalFilename();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            RequestBody body = RequestBody.fromInputStream(
                    file.getInputStream(),
                    file.getSize()
            );

            s3Client.putObject(request, body);

            return key;
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file: " + e);
        }
    }

    @Override
    public byte[] download(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);

            return response.asByteArray();

        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new ResourceNotFoundException("File not found: " + key);
            }
            throw new RuntimeException("Error downloading file: " + key, e);
        }
    }

    @Override
    public void delete(String key) {

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }
}
