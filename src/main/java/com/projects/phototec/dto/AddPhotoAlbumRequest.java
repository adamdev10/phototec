package com.projects.phototec.dto;

import jakarta.validation.constraints.NotNull;

public record AddPhotoAlbumRequest(
        @NotNull(message = "album id is required")
        Long id
) {
}
