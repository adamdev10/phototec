package com.projects.phototec.controller;

import com.projects.phototec.dto.*;
import com.projects.phototec.model.User;
import com.projects.phototec.service.AlbumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.midi.Patch;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
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
    public ResponseEntity<AlbumResponse> create(@RequestBody CreateAlbumRequest createAlbumRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        AlbumResponse response = albumService.create(createAlbumRequest, user);

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
    public ResponseEntity<AlbumResponse> update(@PathVariable Long id, @RequestBody PatchAlbumRequest patchAlbumRequest) {
        AlbumResponse response = albumService.update(id, patchAlbumRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<Void> addPhoto(
            @PathVariable Long id,
            @RequestBody AddPhotoAlbumRequest request
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
    ) throws IOException {
        PhotoResponse response = albumService.addNewPhotoToAlbum(id, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


}
