package com.projects.phototec.mapper;

import com.projects.phototec.dto.PhotoResponse;
import com.projects.phototec.model.Photo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PhotoMapper {

    public PhotoResponse toResponse(Photo photo) {
        return new PhotoResponse(
                photo.getId(),
                photo.getFileName(),
                photo.getContentType()
        );
    }

    public List<PhotoResponse> toResponseList(List<Photo> photos) {
        return photos.stream()
                .map(this::toResponse)
                .toList();
    }
}
