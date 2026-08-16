package com.projects.phototec.dto;

public record PhotoResponse(
        Long id,
        String fileName,
        String contentType
)
{ }
