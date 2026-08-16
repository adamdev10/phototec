package com.projects.phototec.dto;

public record UserResponse(
        Long id,
        String username,
        String firstname,
        String lastname
) {
}
