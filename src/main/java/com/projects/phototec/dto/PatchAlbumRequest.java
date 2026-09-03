package com.projects.phototec.dto;

import jakarta.validation.constraints.NotBlank;

public record PatchAlbumRequest(
        @NotBlank(message = "name is required")
        String name
) {
}
