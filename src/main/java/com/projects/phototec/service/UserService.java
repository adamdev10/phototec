package com.projects.phototec.service;

import com.projects.phototec.dto.PatchUserRequest;
import com.projects.phototec.dto.UserResponse;
import com.projects.phototec.exception.ResourceAlreadyExistsException;
import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.mapper.UserMapper;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.UserRepository;
import com.projects.phototec.security.Role;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public UserResponse get(Long id) {
        return userMapper.toResponse(getUser(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public UserResponse update(Long id, PatchUserRequest request) {
        User user = getUser(id);
        if (request.username() != null && !request.username().equals(user.getUsername())) {
            checkUserExistsByUsername(request.username());
            user.setUsername(request.username());
        }
        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.firstName() != null){
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null){
            user.setLastName(request.lastName());
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse delete(Long id) {
        User user = getUser(id);
        userRepository.deleteById(id);
        return userMapper.toResponse(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    public void register(String username, String password) {

        checkUserExistsByUsername(username);
        User user = new User(username, passwordEncoder.encode(password), Role.USER);
        userRepository.save(user);
    }

    public void checkUserExistsByUsername(String username) {
        if(userRepository.findByUsername(username).isPresent()) {
            throw new ResourceAlreadyExistsException("User named " + username + " already exists");
        }
    }
}
