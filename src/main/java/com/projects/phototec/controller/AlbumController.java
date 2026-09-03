package com.projects.phototec.controller;

import com.projects.phototec.dto.*;
import com.projects.phototec.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/{id}")
    public AlbumResponse get(@PathVariable Long id){
        return albumService.get(id);
    }

    @GetMapping("/{id}/photos")
    public List<PhotoResponse> getPhotos(@PathVariable Long id) {
        return albumService.getPhotos(id);
    }

    @GetMapping()
    public List<AlbumResponse> getAll() {
        return albumService.getAll();
    }

    @PostMapping()
    public ResponseEntity<AlbumResponse> create(@Valid @RequestBody CreateAlbumRequest createAlbumRequest) {
        AlbumResponse response = albumService.create(createAlbumRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AlbumResponse> delete(@PathVariable Long id) {
        AlbumResponse response = albumService.delete(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlbumResponse> update(@PathVariable Long id, @Valid @RequestBody PatchAlbumRequest patchAlbumRequest) {
        AlbumResponse response = albumService.update(id, patchAlbumRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<Void> addPhoto(
            @PathVariable Long id,
            @Valid @RequestBody AddPhotoAlbumRequest request
    ) {
        albumService.addPhotoToAlbum(id, request.id());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{albumId}/photos/{photoId}")
    public ResponseEntity<Void> removePhoto(@PathVariable Long albumId, @PathVariable Long photoId) {
        albumService.removePhotoFromAlbum(albumId, photoId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/photos/upload")
    public ResponseEntity<PhotoResponse> addNewPhoto(
            @PathVariable Long id,
            @RequestParam MultipartFile file
    ){
        PhotoResponse response = albumService.addNewPhotoToAlbum(id, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


}
