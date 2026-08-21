package com.projects.phototec.security;

import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.model.Photo;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.PhotoRepository;
import org.springframework.stereotype.Component;

@Component
public class PhotoSecurity {

    private final PhotoRepository photoRepository;

    private final CurrentUser currentUser;

    public PhotoSecurity(
            PhotoRepository photoRepository,
            CurrentUser currentUser
    ) {
        this.photoRepository = photoRepository;
        this.currentUser = currentUser;
    }

    public boolean isOwner(Long photoId) {
        Photo photo = photoRepository
                .findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo with id: " + photoId + " not found."));

        User user = currentUser.get();
        return photo.getOwner().getId().equals(user.getId());
    }
}
