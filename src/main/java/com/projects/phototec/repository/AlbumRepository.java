package com.projects.phototec.repository;

import com.projects.phototec.model.Album;
import com.projects.phototec.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findAlbumByName(String name);

    Optional<Album> findAlbumByNameAndOwnerId(String name, Long ownerId);

    List<Album> findAllByOwnerId(Long id);
}
