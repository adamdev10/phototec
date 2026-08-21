package com.projects.phototec.repository;

import com.projects.phototec.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByOwnerId(Long ownerId);
}
