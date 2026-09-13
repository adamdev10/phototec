package com.projects.phototec.service;


import com.projects.phototec.dto.PatchPhotoRequest;
import com.projects.phototec.dto.PhotoResponse;
import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.mapper.PhotoMapper;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.Photo;
import com.projects.phototec.repository.PhotoRepository;
import com.projects.phototec.security.CurrentUser;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;
    private final CurrentUser currentUser;
    private final StorageService storageService;


    public PhotoService(
            PhotoRepository photoRepository,
            PhotoMapper photoMapper,
            CurrentUser currentUser,
            StorageService storageService
    ) {
        this.photoRepository = photoRepository;
        this.photoMapper = photoMapper;
        this.currentUser = currentUser;
        this.storageService = storageService;
    }

    @PreAuthorize("@photoSecurity.isOwner(#id)")
    public PhotoResponse get(Long id) {
        return photoMapper.toResponse(getPhoto(id));
    }

    @PreAuthorize("@photoSecurity.isOwner(#id)")
    public byte[] getContent(Long id) {
        Photo photo = getPhoto(id);
        return storageService.download(photo.getKey());
    }

    public Photo getPhoto(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PhotoResponse> getAll() {
        return photoMapper.toResponseList(photoRepository.findAll());
    }

    public PhotoResponse create(MultipartFile file){
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        return photoMapper.toResponse(savePhoto(file));
    }

    @PreAuthorize("@photoSecurity.isOwner(#id)")
    @Transactional
    public PhotoResponse delete(Long id) {
        Photo photo = getPhoto(id);

        for(Album album: new ArrayList<>(photo.getAlbums())) {
            album.removePhoto(photo);
        }

        storageService.delete(photo.getKey());
        photoRepository.delete(photo);
        return photoMapper.toResponse(photo);
    }

    public Photo savePhoto(MultipartFile file) {
        String key = storageService.upload(file);

        return photoRepository.save(
                new Photo(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        key,
                        currentUser.get()
                )
        );
    }

    @PreAuthorize("@photoSecurity.isOwner(#id)")
    public PhotoResponse replace(Long id, MultipartFile file) {
        Photo photo = getPhoto(id);
        String oldKey = photo.getKey();
        String newKey = storageService.upload(file);
        photo.setKey(newKey);
        photo.setFileName(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        PhotoResponse response = photoMapper.toResponse(photoRepository.save(photo));
        storageService.delete(oldKey);
        return response;
    }

    @PreAuthorize("@photoSecurity.isOwner(#id)")
    public PhotoResponse update(Long id, PatchPhotoRequest patchPhotoRequest) {
        Photo photo = getPhoto(id);
        photo.setFileName(patchPhotoRequest.fileName() + "." + photo.getContentType().split("/")[1]);

        return photoMapper.toResponse(photoRepository.save(photo));
    }


    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public List<PhotoResponse> getUserPhotosById(Long id) {
        return photoRepository.findAllByOwnerId(id)
                .stream()
                .map(photoMapper::toResponse)
                .toList();
    }

}
