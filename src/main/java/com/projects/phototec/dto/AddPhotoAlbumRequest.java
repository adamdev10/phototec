package com.projects.phototec.dto;

import jakarta.validation.constraints.NotBlank;

public record AddPhotoAlbumRequest(
        @NotBlank
        Long id
) {
}
