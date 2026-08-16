package com.projects.phototec.mapper;

import com.projects.phototec.dto.AlbumResponse;
import com.projects.phototec.dto.PhotoResponse;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.Photo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlbumMapper {

    public AlbumResponse toResponse(Album album) {
        return new AlbumResponse(
                album.getId(),
                album.getName()
        );
    }

    public List<AlbumResponse> toResponseList(List<Album> albums) {
        return albums.stream()
                .map(this::toResponse)
                .toList();
    }
}
