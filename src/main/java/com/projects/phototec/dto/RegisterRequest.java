package com.projects.phototec.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "username is required")
        String username,

        @NotBlank(message = "password is required")
        String password
) {
}
