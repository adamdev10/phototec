package com.projects.phototec.controller;

import com.projects.phototec.dto.CreatePhotoRequest;
import com.projects.phototec.dto.PatchPhotoRequest;
import com.projects.phototec.dto.PhotoResponse;
import com.projects.phototec.model.Photo;
import com.projects.phototec.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }


    @GetMapping("/{id}")
    public PhotoResponse get(@PathVariable Long id) {
        return photoService.get(id);
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<byte[]> getContent(@PathVariable Long id) {
        Photo photo = photoService.getPhoto(id);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(photo.getContentType())
                )
                .body(photo.getData());
    }

    @GetMapping()
    public List<PhotoResponse> getAll() {
        return photoService.getAll();
    }

    @PostMapping
    public ResponseEntity<PhotoResponse> create(@RequestParam MultipartFile file) throws IOException {
        PhotoResponse response = photoService.create(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PhotoResponse> delete(@PathVariable Long id) {
        PhotoResponse response = photoService.delete(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhotoResponse> replace(
            @PathVariable Long id,
            @RequestParam MultipartFile file
    ) throws IOException {
        PhotoResponse response = photoService.replace(id, file);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PhotoResponse> update(
            @PathVariable Long id,
            @RequestBody PatchPhotoRequest patchPhotoRequest
    ) {
        PhotoResponse response = photoService.update(id, patchPhotoRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



}
