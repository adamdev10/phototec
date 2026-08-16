package com.projects.phototec.mapper;

import com.projects.phototec.dto.PhotoResponse;
import com.projects.phototec.dto.UserResponse;
import com.projects.phototec.model.Photo;
import com.projects.phototec.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}
