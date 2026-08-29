package com.projects.phototec.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface StorageService {

    String upload(MultipartFile file);

    byte[] download(String key);

    void delete(String key);
}
