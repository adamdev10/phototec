package com.projects.phototec.security;


import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    private final CurrentUser currentUser;

    public UserSecurity(
            CurrentUser currentUser
    ) {
        this.currentUser = currentUser;
    }

    public boolean isOwner(Long userId) {
        return currentUser.get().getId().equals(userId);
    }
}
