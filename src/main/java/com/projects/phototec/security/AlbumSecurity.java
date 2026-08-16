package com.projects.phototec.security;

import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.AlbumRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AlbumSecurity {

    private final AlbumRepository albumRepository;

    public AlbumSecurity(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public boolean isOwner(Long albumId, Authentication authentication) {
        Album album = albumRepository
                .findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album with id: " + albumId + " not found."));

        User user = (User) authentication.getPrincipal();
        return album.getOwner().getId().equals(user.getId());
    }
}
