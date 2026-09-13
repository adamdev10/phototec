package com.projects.phototec.controller;

import com.projects.phototec.dto.LoginRequest;
import com.projects.phototec.dto.PatchUserRequest;
import com.projects.phototec.dto.RegisterRequest;
import com.projects.phototec.model.Album;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.AlbumRepository;
import com.projects.phototec.repository.UserRepository;
import com.projects.phototec.security.Role;
import com.projects.phototec.service.AuthService;
import com.projects.phototec.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User createUser(String username) {
        User user = new User(username, "password", Role.USER);

        return userRepository.save(user);
    }

    private User createAdminUser(String username) {
        User user = new User(username, "password", Role.ADMIN);

        return userRepository.save(user);
    }

    private Album createAlbum(String name, User owner) {
        Album album = new Album(name, owner);

        return albumRepository.save(album);
    }

    @Test
    void getCurrentUser() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                get("/users/me").with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getMyUser() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                get("/users/{id}", user.getId()).with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getMyUserUnauthenticated() throws Exception {

        mockMvc.perform(
                get("/users/{id}", 1L)
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void getOtherUser() throws Exception {

        User realUser = createUser("realuser");
        User user = createUser("tester");

        mockMvc.perform(
                get("/users/{id}", realUser.getId()).with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void getNonexistentUser() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                get("/users/{id}", 222L).with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void getOtherUserAsAdmin() throws Exception {

        User realUser = createUser("realuser");
        User admin = createAdminUser("admin");

        mockMvc.perform(
                get("/users/{id}", realUser.getId()).with(user(admin))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getAllUsers() throws Exception {

        User admin = createAdminUser("admin");

        mockMvc.perform(
                get("/users").with(user(admin))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getAllUsersAsNormalUser() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                get("/users").with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void register() throws Exception {

        RegisterRequest request = new RegisterRequest("tester", "password");

        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))

        ).andExpect(
                status().isNoContent()
        );
    }

    @Test
    void registerBlankUsername() throws Exception {

        RegisterRequest request = new RegisterRequest("", "password");

        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))

        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void registerBlankPassword() throws Exception {

        RegisterRequest request = new RegisterRequest("tester", "");

        mockMvc.perform(
                post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))

        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void login() throws Exception {

        String username = "tester";
        String password = "password";

        userService.register(username, password);

        LoginRequest request = new LoginRequest(username, password);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void loginNonexistentUser() throws Exception {

        String username = "tester";
        String password = "password";

        LoginRequest request = new LoginRequest(username, password);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void loginBadCredentials() throws Exception {

        String username = "tester";
        String password = "password";

        userService.register(username, password);

        LoginRequest request = new LoginRequest(username, "wrongpass");

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void loginBlankUsername() throws Exception {

        String username = "tester";
        String password = "password";

        userService.register(username, password);

        LoginRequest request = new LoginRequest("", password);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void loginBlankPassword() throws Exception {

        String username = "tester";
        String password = "password";

        userService.register(username, password);

        LoginRequest request = new LoginRequest(username, "");

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void loginNullUsername() throws Exception {

        String username = "tester";
        String password = "password";

        userService.register(username, password);

        LoginRequest request = new LoginRequest(null, password);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void updateMyUser() throws Exception {

        User user = createUser("tester");

        PatchUserRequest request = new PatchUserRequest("newname", null, null, null);

        mockMvc.perform(
                patch("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void updateOtherUser() throws Exception {

        User other = createUser("other");
        User user  = createUser("tester");

        PatchUserRequest request = new PatchUserRequest("newname", null, null, null);

        mockMvc.perform(
                patch("/users/{id}", other.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void updateUserAsAdmin() throws Exception {

        User other = createUser("other");
        User admin = createAdminUser("admin");

        PatchUserRequest request = new PatchUserRequest("newname", null, null, null);

        mockMvc.perform(
                patch("/users/{id}", other.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(admin))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void updateUserDuplicateUsername() throws Exception {

        createUser("existing");
        User user = createUser("tester");

        PatchUserRequest request = new PatchUserRequest("existing", null, null, null);

        mockMvc.perform(
                patch("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(user))
        ).andExpect(
                status().isConflict()
        );
    }


    @Test
    void deleteUserAsAdmin() throws Exception {

        User other = createUser("victim");
        User admin = createAdminUser("admin");

        mockMvc.perform(
                delete("/users/{id}", other.getId())
                        .with(user(admin))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void deleteUserAsNormalUser() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                delete("/users/{id}", user.getId())
                        .with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void deleteNonexistentUserAsAdmin() throws Exception {

        User admin = createAdminUser("admin");

        mockMvc.perform(
                delete("/users/{id}", 999L)
                        .with(user(admin))
        ).andExpect(
                status().isNotFound()
        );
    }


    @Test
    void getMyAlbums() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                get("/users/{id}/albums", user.getId())
                        .with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getOtherUserAlbums() throws Exception {

        User other = createUser("other");
        User user  = createUser("tester");

        mockMvc.perform(
                get("/users/{id}/albums", other.getId())
                        .with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void getOtherUserAlbumsAsAdmin() throws Exception {

        User other = createUser("other");
        User admin = createAdminUser("admin");

        mockMvc.perform(
                get("/users/{id}/albums", other.getId())
                        .with(user(admin))
        ).andExpect(
                status().isOk()
        );
    }


    @Test
    void getMyPhotos() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                get("/users/{id}/photos", user.getId())
                        .with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getOtherUserPhotos() throws Exception {

        User other = createUser("other");
        User user  = createUser("tester");

        mockMvc.perform(
                get("/users/{id}/photos", other.getId())
                        .with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void getOtherUserPhotosAsAdmin() throws Exception {

        User other = createUser("other");
        User admin = createAdminUser("admin");

        mockMvc.perform(
                get("/users/{id}/photos", other.getId())
                        .with(user(admin))
        ).andExpect(
                status().isOk()
        );
    }
}
