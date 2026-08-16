package com.projects.phototec.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePhotoRequest(
        @NotBlank(message = "File name is required")
        String fileName,

        @NotBlank(message = "Content type is required")
        String contentType,

        @NotBlank(message = "Photo data is required")
        byte[] data
) { }
