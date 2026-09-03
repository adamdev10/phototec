package com.projects.phototec.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAlbumRequest(
        @NotBlank(message = "album name is required")
        String name
) {
}
