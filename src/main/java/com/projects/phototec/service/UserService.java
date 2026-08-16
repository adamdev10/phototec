package com.projects.phototec.service;

import com.projects.phototec.dto.UserResponse;
import com.projects.phototec.exception.ResourceAlreadyExistsException;
import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.mapper.UserMapper;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.UserRepository;
import com.projects.phototec.security.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @PreAuthorize("#id == authentication.principal.id")
    public UserResponse get(Long id) {
        return userMapper.toResponse(getUser(id));
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    public void register(String username, String password) {

        if(userRepository.findByUsername(username).isPresent()) {
            throw new ResourceAlreadyExistsException("User named " + username + " already exists");
        }
        User user = new User(username, passwordEncoder.encode(password), Role.USER);
        userRepository.save(user);
    }
}
