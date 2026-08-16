package com.projects.phototec.album;


import com.projects.phototec.model.Album;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.AlbumRepository;
import com.projects.phototec.repository.UserRepository;
import com.projects.phototec.security.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    private User createUser(String username) {
        User user = new User(username, "password", Role.USER);

        return userRepository.save(user);
    }

    private Album createAlbum(String name, User owner) {
        Album album = new Album(name, owner);

        return albumRepository.save(album);
    }

    @Test
    void getAlbum() throws Exception {

        User user = createUser("Tester");

        Album album = createAlbum("Viaje", user);

        mockMvc.perform(
                get("/albums/{id}", album.getId()).with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getOtherUserAlbum() throws Exception {

        User owner = createUser("Owner");

        Album album = createAlbum("Tokyo", owner);

        User other = createUser("Stealer");

        mockMvc.perform(
                get("/albums/{id}", album.getId()).with(user(other))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void getAlbumNotAuthenticated() throws Exception {

        User user = createUser("ImNotHere");

        Album album = createAlbum("Voyage", user);

        mockMvc.perform(
                get("/albums/{id}", album.getId())
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void getAlbumNotFound() throws Exception {

        User user = createUser("Bob");

        mockMvc.perform(
                get("/albums/{id}", 22L).with(user(user))
        ).andExpect(
                status().isNotFound()
        );
    }
}
