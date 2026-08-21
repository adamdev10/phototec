package com.projects.phototec.controller;

import com.projects.phototec.dto.*;
import com.projects.phototec.model.Photo;
import com.projects.phototec.service.AlbumService;
import com.projects.phototec.service.PhotoService;
import com.projects.phototec.service.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AlbumService albumService;
    private final PhotoService photoService;


    public UserController(
            UserService userService,
            AlbumService albumService,
            PhotoService photoService
    ) {
        this.userService = userService;
        this.albumService = albumService;
        this.photoService = photoService;
    }


    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest) {

        userService.register(registerRequest.username(), registerRequest.password());

        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody PatchUserRequest patchUserRequest
    ) {
        UserResponse response = userService.update(id, patchUserRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> delete(@PathVariable Long id) {
        UserResponse response = userService.delete(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @GetMapping("/{id}/albums")
    public List<AlbumResponse> getAlbums(@PathVariable Long id) {
        return albumService.getUserAlbumsById(id);
    }

    @GetMapping("/{id}/photos")
    public List<PhotoResponse> getPhotos(@PathVariable Long id) {
        return photoService.getUserPhotosById(id);
    }
}
