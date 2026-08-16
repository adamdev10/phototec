package com.projects.phototec.service;

import com.projects.phototec.dto.AlbumResponse;
import com.projects.phototec.dto.CreateAlbumRequest;
import com.projects.phototec.dto.PatchAlbumRequest;
import com.projects.phototec.dto.PhotoResponse;
import com.projects.phototec.exception.ResourceAlreadyExistsException;
import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.mapper.AlbumMapper;
import com.projects.phototec.mapper.PhotoMapper;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.Photo;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.AlbumRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    private final AlbumMapper albumMapper;

    private final PhotoMapper photoMapper;
    private final PhotoService photoService;

    public AlbumService(
            AlbumRepository albumRepository,
            AlbumMapper albumMapper,
            PhotoMapper photoMapper,
            PhotoService photoService
    ) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
        this.photoMapper = photoMapper;
        this.photoService = photoService;
    }

    @PreAuthorize("@albumSecurity.isOwner(#id, authentication)")
    public AlbumResponse get(Long id){
        Album album = getAlbum(id);
        return albumMapper.toResponse(album);
    }

    public List<AlbumResponse> getAll() {
        return albumMapper.toResponseList(albumRepository.findAll());
    }

    public List<PhotoResponse> getPhotos(Long id) {
        Album album = getAlbum(id);
        return photoMapper.toResponseList(album.getPhotos());
    }

    public AlbumResponse create(CreateAlbumRequest createAlbumRequest, User owner) {
        existsByName(createAlbumRequest.name());
        return albumMapper.toResponse(albumRepository.save(new Album(createAlbumRequest.name(), owner)));
    }

    public AlbumResponse delete(Long id){
        AlbumResponse album = get(id);
        albumRepository.deleteById(id);
        return album;
    }

    public AlbumResponse update(Long id, PatchAlbumRequest patchAlbumRequest) {
        existsByName(patchAlbumRequest.name());
        Album album = getAlbum(id);
        album.setName(patchAlbumRequest.name());
        return albumMapper.toResponse(albumRepository.save(album));
    }

    @Transactional
    public void addPhotoToAlbum(Long albumId, Long photoId) {
        Album album = getAlbum(albumId);
        Photo photo = photoService.getPhoto(photoId);

        if (album.getPhotos().contains(photo)) {
            throw new ResourceAlreadyExistsException(
                    "Photo with id: " + photoId + " already exists in album with id: " + albumId
            );
        }

        album.addPhoto(photo);
    }

    @Transactional
    public void removePhotoFromAlbum(Long albumId, Long photoId) {
        Album album = getAlbum(albumId);
        Photo photo = photoService.getPhoto(photoId);

        if (!album.getPhotos().contains(photo)) {
            throw new ResourceNotFoundException(
                    "Photo " + photoId + " not found in album " + albumId
            );
        }

        album.removePhoto(photo);
    }

    @Transactional
    public PhotoResponse addNewPhotoToAlbum(Long albumId, MultipartFile file) throws IOException {
        Album album = getAlbum(albumId);
        Photo photo = photoService.savePhoto(file);

        album.addPhoto(photo);

        return photoMapper.toResponse(photo);
    }



    public Album getAlbum(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + id));
    }

    private void existsByName(String name) {
        if (!albumRepository.findAlbumByName(name).isEmpty())
            throw new ResourceAlreadyExistsException("Album already exists, name: " + name);
    }
}
