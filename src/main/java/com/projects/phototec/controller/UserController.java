package com.projects.phototec.controller;

import com.projects.phototec.dto.RegisterRequest;
import com.projects.phototec.dto.UserResponse;
import com.projects.phototec.service.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test(Authentication authentication) {

        return authentication.name();
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest) {

        userService.register(registerRequest.username(), registerRequest.password());

        return ResponseEntity
                .noContent()
                .build();
    }
}
