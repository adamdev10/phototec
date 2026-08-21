package com.projects.phototec.repository;

import com.projects.phototec.model.Album;
import com.projects.phototec.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findAlbumByName(String name);

    List<Album> findAllByOwnerId(Long id);
}
