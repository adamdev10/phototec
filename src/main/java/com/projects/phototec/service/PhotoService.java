package com.projects.phototec.service;

import com.projects.phototec.dto.CreatePhotoRequest;
import com.projects.phototec.dto.PatchPhotoRequest;
import com.projects.phototec.dto.PhotoResponse;
import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.mapper.PhotoMapper;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.Photo;
import com.projects.phototec.repository.PhotoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;

    public PhotoService(PhotoRepository photoRepository, PhotoMapper photoMapper) {
        this.photoRepository = photoRepository;
        this.photoMapper = photoMapper;
    }

    public PhotoResponse get(Long id) {
        return photoMapper.toResponse(getPhoto(id));
    }

    public Photo getPhoto(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + id));
    }

    public List<PhotoResponse> getAll() {
        return photoMapper.toResponseList(photoRepository.findAll());

    }

    public PhotoResponse create(MultipartFile file) throws IOException {
        return photoMapper.toResponse(savePhoto(file));
    }


    @Transactional
    public PhotoResponse delete(Long id) {
        Photo photo = getPhoto(id);

        for(Album album: new ArrayList<>(photo.getAlbums())) {
            album.removePhoto(photo);
        }

        photoRepository.delete(photo);
        return photoMapper.toResponse(photo);
    }

    public Photo savePhoto(MultipartFile file) throws IOException {
        return photoRepository.save(
                new Photo(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                )
        );
    }

    public PhotoResponse replace(Long id, MultipartFile file) throws IOException {
        Photo photo = getPhoto(id);
        photo.setFileName(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setData(file.getBytes());
        return photoMapper.toResponse(photoRepository.save(photo));
    }

    public PhotoResponse update(Long id, PatchPhotoRequest patchPhotoRequest) {
        Photo photo = getPhoto(id);
        photo.setFileName(patchPhotoRequest.fileName() + "." + photo.getContentType().split("/")[1]);

        return photoMapper.toResponse(photoRepository.save(photo));
    }

}
