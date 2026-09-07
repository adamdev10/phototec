package com.projects.phototec.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.projects.phototec.dto.PatchPhotoRequest;
import com.projects.phototec.dto.PhotoResponse;
import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.mapper.PhotoMapper;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.Photo;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.PhotoRepository;
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

@ExtendWith(MockitoExtension.class)
public class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private CurrentUser currentUser;

    @Spy
    private PhotoMapper photoMapper;

    @InjectMocks
    private PhotoService photoService;

    @Test
    void getPhoto() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        Photo photo = new Photo("selfie.png", "image/png", "123-456-789", owner);

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        assertEquals(photo, photoService.getPhoto(1L));
    }

    @Test
    void getNonexistentPhoto() {
        when(photoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> photoService.getPhoto(1L));
    }

    @Test
    void getAll() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        Photo photo1 = new Photo("selfie.png", "image/png", "123-456-789", owner);
        Photo photo2 = new Photo("morning.png", "image/png", "321-456-789", owner);

        when(photoRepository.findAll()).thenReturn(List.of(photo1, photo2));

        assertEquals(photoMapper.toResponseList(List.of(photo1, photo2)), photoService.getAll());
    }

    @Test
    void createPhoto() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "bytes".getBytes());
        Photo photo = new Photo("test.png", "image/png", "some-key", owner);

        when(currentUser.get()).thenReturn(owner);
        when(storageService.upload(file)).thenReturn("some-key");
        when(photoRepository.save(any(Photo.class))).thenReturn(photo);

        PhotoResponse response = photoService.create(file);

        assertNotNull(response);
        verify(storageService).upload(file);
        verify(photoRepository).save(any(Photo.class));
    }

    @Test
    void createPhotoWithEmptyFile() {
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);

        assertThrowsExactly(IllegalArgumentException.class, () -> photoService.create(file));
    }

    @Test
    void createPhotoWithNullFile() {
        assertThrowsExactly(IllegalArgumentException.class, () -> photoService.create(null));
    }

    @Test
    void deletePhoto() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        Photo photo = new Photo("selfie.png", "image/png", "some-key", owner);

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        photoService.delete(1L);

        verify(storageService).delete("some-key");
        verify(photoRepository).delete(photo);
    }

    @Test
    void deleteNonexistentPhoto() {
        when(photoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> photoService.delete(1L));
    }

    @Test
    void deletePhotoRemovesItFromAlbums() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        Photo photo = new Photo("selfie.png", "image/png", "some-key", owner);
        Album album = new Album("Me", owner);
        album.addPhoto(photo);

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        photoService.delete(1L);

        assertFalse(album.getPhotos().contains(photo));
    }

    @Test
    void replacePhoto() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        Photo photo = new Photo("old.png", "image/png", "old-key", owner);
        MultipartFile file = new MockMultipartFile("file", "new.png", "image/png", "bytes".getBytes());

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(storageService.upload(file)).thenReturn("new-key");
        when(photoRepository.save(any(Photo.class))).thenReturn(photo);

        photoService.replace(1L, file);

        verify(storageService).upload(file);
        verify(storageService).delete("old-key");
        verify(photoRepository).save(photo);
        assertEquals("new-key", photo.getKey());
    }

    @Test
    void updatePhoto() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        Photo photo = new Photo("old.png", "image/png", "some-key", owner);

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(photoRepository.save(any(Photo.class))).thenReturn(photo);

        photoService.update(1L, new PatchPhotoRequest("new-name"));

        verify(photoRepository).save(photo);
        assertTrue(photo.getFileName().startsWith("new-name"));
    }

    @Test
    void getUserPhotosById() {
        User owner = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        Photo photo1 = new Photo("selfie.png", "image/png", "123-456-789", owner);
        Photo photo2 = new Photo("morning.png", "image/png", "321-456-789", owner);

        when(photoRepository.findAllByOwnerId(1L)).thenReturn(List.of(photo1, photo2));

        assertEquals(photoMapper.toResponseList(List.of(photo1, photo2)), photoService.getUserPhotosById(1L));
    }
}