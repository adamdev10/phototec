package com.projects.phototec.security;

import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.AlbumRepository;
import org.springframework.stereotype.Component;

@Component
public class AlbumSecurity {

    private final AlbumRepository albumRepository;

    private final CurrentUser currentUser;

    public AlbumSecurity(
            AlbumRepository albumRepository,
            CurrentUser currentUser
    ) {
        this.albumRepository = albumRepository;
        this.currentUser = currentUser;
    }

    public boolean isOwner(Long albumId) {
        Album album = albumRepository
                .findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album with id: " + albumId + " not found."));

        User user = currentUser.get();
        return album.getOwner().getId().equals(user.getId());
    }
}
