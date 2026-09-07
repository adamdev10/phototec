package com.projects.phototec.service;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.projects.phototec.dto.PatchUserRequest;
import com.projects.phototec.exception.ResourceAlreadyExistsException;
import com.projects.phototec.exception.ResourceNotFoundException;
import com.projects.phototec.mapper.UserMapper;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.UserRepository;
import com.projects.phototec.security.CurrentUser;
import com.projects.phototec.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CurrentUser currentUser;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;


    @Test
    void getUser() {

        User user = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertEquals(userMapper.toResponse(user), userService.get(1L));
    }

    @Test
    void getNonexistentUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> userService.get(1L));
    }

    @Test
    void getCurrentUser() {

        User user = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        when(currentUser.get()).thenReturn(user);

        assertEquals(userMapper.toResponse(user), userService.getCurrentUser());
    }

    @Test
    void getAllUsers() {

        User user1 = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        User user2 = new User("Bob2", "{bcrypt}HelloWorld2", Role.USER);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        assertEquals(userMapper.toResponseList(List.of(user1, user2)), userService.getAll());
    }

    @Test
    void updateUsername() {

        User user = new User("Bob", "{bcrypt}HelloWorld", Role.USER);
        User user2 = new User("Bobby", "{bcrypt}HelloWorld", Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user2);

        PatchUserRequest request = new PatchUserRequest("Bobby", null, null, null);

        assertEquals("Bobby", userService.update(1L, request).username());
    }

    @Test
    void deleteUser() {

        User user = new User("Bob", "{bcrypt}HelloWorld", Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteNonexistentUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> userService.delete(1L));
    }

    @Test
    void registerUser() {

        String username = "Bob";
        String password = "HelloWorld";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        when(passwordEncoder.encode(password)).thenReturn("{bcrypt}awudasbdasd");

        userService.register(username, password);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerAlreadyExistingUser() {

        String username = "Bob";
        String password = "HelloWorld";

        User user = new User(username, password, Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrowsExactly(ResourceAlreadyExistsException.class, () -> userService.register(username, password));

    }


}
