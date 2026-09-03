package com.projects.phototec.dto;

import jakarta.validation.constraints.NotBlank;

public record PatchPhotoRequest(
        @NotBlank(message = "fileName is required")
        String fileName
) {}
