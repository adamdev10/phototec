package com.projects.phototec.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.projects.phototec.dto.CreateAlbumRequest;
import com.projects.phototec.dto.PatchAlbumRequest;
import com.projects.phototec.exception.ResourceAlreadyExistsException;
import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.mapper.AlbumMapper;
import com.projects.phototec.mapper.PhotoMapper;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.Photo;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.AlbumRepository;
import com.projects.phototec.repository.UserRepository;
import com.projects.phototec.security.CurrentUser;
import com.projects.phototec.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PhotoService photoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUser currentUser;

    @Spy
    private AlbumMapper albumMapper;

    @Spy
    private PhotoMapper photoMapper;

    @InjectMocks
    private AlbumService albumService;


    @Test
    void getAlbum() {

        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        assertEquals(albumMapper.toResponse(album), albumService.get(1L));
    }

    @Test
    void getNonexistentAlbum() {

        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> albumService.get(1L));
    }

    @Test
    void getAllAlbums() {

        User owner1 = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        User owner2 = new User("Bob2", "{bcrypt}HelloWorld2", Role.USER);


        Album album1 = new Album("Me", owner1);
        Album album2 = new Album("Family", owner1);
        Album album3 = new Album("London", owner2);

        when(albumRepository.findAll()).thenReturn(List.of(album1, album2, album3));

        assertEquals(albumMapper.toResponseList(List.of(album1, album2, album3)), albumService.getAll());
    }

    @Test
    void getAlbumPhotos() {

        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        Photo photo = new Photo("selfie.png", "image/png", "123-456-789", owner);
        Photo photo2 = new Photo("morning.png", "image/png", "321-456-789", owner);

        album.addPhoto(photo);
        album.addPhoto(photo2);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        assertEquals(photoMapper.toResponseList(List.of(photo, photo2)), albumService.getPhotos(1L));
    }

    @Test
    void createAlbum() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        when(albumRepository.save(any(Album.class))).thenReturn(album);

        when(currentUser.get()).thenReturn(owner);

        assertEquals(albumMapper.toResponse(album), albumService.create(new CreateAlbumRequest("Me")));
    }

    @Test
    void createAlreadyExistingAlbum() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        owner.setId(1L);

        Album album = new Album("Me", owner);

        when(albumRepository.findAlbumByNameAndOwnerId(eq("Me"), any(Long.class))).thenReturn(Optional.of(album));

        when(currentUser.get()).thenReturn(owner);

        assertThrowsExactly(ResourceAlreadyExistsException.class, () -> albumService.create(new CreateAlbumRequest("Me")));
    }

    @Test
    void deleteAlbum() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        albumService.delete(1L);

        verify(albumRepository).deleteById(1L);
    }

    @Test
    void deleteNonexistentAlbum() {

        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> albumService.delete(1L));
    }

    @Test
    void updateAlbumName() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(albumRepository.save(album)).thenReturn(album);

        when(currentUser.get()).thenReturn(owner);

        assertNotEquals(albumMapper.toResponse(album).name(), albumService.update(1L, new PatchAlbumRequest("NotMe")).name());
    }

    @Test
    void updateAlreadyExistingAlbumName() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        when(currentUser.get()).thenReturn(owner);

        assertThrowsExactly(ResourceNotFoundException.class, () -> albumService.update(1L, new PatchAlbumRequest("NotMe")));
    }

    @Test
    void addPhotoToAlbum() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        Photo photo = new Photo("selfie.png", "image/png", "123-456-789", owner);

        when(photoService.getPhoto(1L)).thenReturn(photo);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        albumService.addPhotoToAlbum(1L, 1L);

        assertTrue(album.getPhotos().contains(photo));
    }

    @Test
    void addAlreadyExistingPhotoToAlbum() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        Photo photo = new Photo("selfie.png", "image/png", "123-456-789", owner);

        album.addPhoto(photo);

        when(photoService.getPhoto(1L)).thenReturn(photo);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        assertThrowsExactly(ResourceAlreadyExistsException.class, () -> albumService.addPhotoToAlbum(1L, 1L));
    }

    @Test
    void removePhotoFromAlbum() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        Photo photo = new Photo("selfie.png", "image/png", "123-456-789", owner);

        album.addPhoto(photo);

        when(photoService.getPhoto(1L)).thenReturn(photo);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        albumService.removePhotoFromAlbum(1L, 1L);

        assertFalse(album.getPhotos().contains(photo));
    }

    @Test
    void removeNonexistentPhotoFromAlbum() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        Photo photo = new Photo("selfie.png", "image/png", "123-456-789", owner);

        when(photoService.getPhoto(1L)).thenReturn(photo);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        assertThrowsExactly(ResourceNotFoundException.class, () -> albumService.removePhotoFromAlbum(1L, 1L));
    }

    @Test
    void addNewPhotoToAlbum() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album = new Album("Me", owner);

        Photo photo = new Photo("selfie.png", "image/png", "123-456-789", owner);

        byte[] data = "test bytes".getBytes();
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", data);

        when(photoService.savePhoto(file)).thenReturn(photo);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        albumService.addNewPhotoToAlbum(1L, file);

        assertTrue(album.getPhotos().contains(photo));
    }

    @Test
    void getUserAlbumsById() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        Album album1 = new Album("Me", owner);
        Album album2 = new Album("London", owner);

        when(albumRepository.findAllByOwnerId(1L)).thenReturn(List.of(album1, album2));

        assertEquals(albumMapper.toResponseList(List.of(album1, album2)), albumService.getUserAlbumsById(1L));
    }
}
