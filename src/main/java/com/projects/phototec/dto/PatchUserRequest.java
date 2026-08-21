package com.projects.phototec.dto;

public record PatchUserRequest(
        String username,
        String firstName,
        String lastName,
        String password
) { }
