package com.projects.phototec.dto;

public record LoginResponse(
        String token,
        Long userId
) {
}
