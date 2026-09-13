package com.projects.phototec.controller;


import com.projects.phototec.config.S3Properties;
import com.projects.phototec.dto.PatchPhotoRequest;
import com.projects.phototec.model.Photo;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.PhotoRepository;
import com.projects.phototec.repository.UserRepository;
import com.projects.phototec.security.Role;
import com.projects.phototec.service.PhotoService;
import com.projects.phototec.service.StorageService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import tools.jackson.databind.ObjectMapper;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Properties s3Properties;

    private final ObjectMapper objectMapper = new ObjectMapper();


    private User createUser(String username) {
        User user = new User(username, "password", Role.USER);

        return userRepository.save(user);
    }

    private User createAdminUser(String username) {
        User user = new User(username, "password", Role.ADMIN);

        return userRepository.save(user);
    }

    private Photo createPhoto(String fileName, User owner) {
        String key = storageService.upload(
                new MockMultipartFile("file", fileName, "image/png", "bytes".getBytes())
        );
        Photo photo = new Photo(fileName, "image/png", key, owner);
        return photoRepository.save(photo);
    }

    @AfterEach
    void cleanup() {
        var objects = s3Client.listObjects(r -> r.bucket(s3Properties.bucket()))
                .contents()
                .stream()
                .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                .toList();

        if (!objects.isEmpty()) {
            s3Client.deleteObjects(r -> r
                    .bucket(s3Properties.bucket())
                    .delete(d -> d.objects(objects))
            );
        }
    }

    @Test
    void getPhoto() throws Exception{

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        mockMvc.perform(
                get("/photos/{id}", photo.getId()).with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getNonexistentPhoto() throws Exception{

        User user = createUser("tester");

        mockMvc.perform(
                get("/photos/{id}", 222L).with(user(user))
        ).andExpect(
                status().isNotFound()
        );
    }

    @Test
    void getPhotoNotAuthenticated() throws Exception{

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        mockMvc.perform(
                get("/photos/{id}", photo.getId())
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void getOtherUserPhoto() throws Exception{

        User owner = createUser("realowner");
        Photo photo = createPhoto("test.png", owner);

        User user = createUser("tester");

        mockMvc.perform(
                get("/photos/{id}", photo.getId()).with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void getAllPhotos() throws Exception{

        User user = createAdminUser("admin");
        createPhoto("test.png", user);

        mockMvc.perform(
                get("/photos").with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getAllPhotosNotAuthenticated() throws Exception{

        User user = createAdminUser("admin");
        createPhoto("test.png", user);

        mockMvc.perform(
                get("/photos")
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void getAllPhotosNotAdmin() throws Exception{

        User user = createUser("tester");
        createPhoto("test.png", user);

        mockMvc.perform(
                get("/photos").with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void getPhotoContent() throws Exception {

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        mockMvc.perform(
                get("/photos/{id}/content", photo.getId()).with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void getPhotoContentNotFound() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                get("/photos/{id}/content", 222L).with(user(user))
        ).andExpect(
                status().isNotFound()
        );
    }

    @Test
    void getPhotoContentNotAuthenticated() throws Exception {

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        mockMvc.perform(
                get("/photos/{id}/content", photo.getId())
        ).andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void getOtherUserPhotoContent() throws Exception {

        User owner = createUser("realowner");
        Photo photo = createPhoto("test.png", owner);

        User user = createUser("tester");

        mockMvc.perform(
                get("/photos/{id}/content", photo.getId()).with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void createPhoto() throws Exception {

        User user = createUser("tester");

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "bytes".getBytes());

        mockMvc.perform(
                multipart("/photos").file(file).with(user(user))
        ).andExpect(
                status().isCreated()
        );
    }

    @Test
    void createEmptyFilePhoto() throws Exception {

        User user = createUser("tester");

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);

        mockMvc.perform(
                multipart("/photos").file(file).with(user(user))
        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void createNullFilePhoto() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                multipart("/photos").with(user(user))
        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void deletePhoto() throws Exception {

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        mockMvc.perform(
                delete("/photos/{id}", photo.getId()).with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void deleteNonexistentPhoto() throws Exception {

        User user = createUser("tester");

        mockMvc.perform(
                delete("/photos/{id}", 1L).with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void replacePhoto() throws Exception {

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "bytes".getBytes());

        mockMvc.perform(
                multipart("/photos/{id}", photo.getId())
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; })
                        .with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void replaceNonexistentPhoto() throws Exception {

        User user = createUser("tester");

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "bytes".getBytes());

        mockMvc.perform(
                multipart("/photos/{id}", 1L)
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; })
                        .with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void replaceOtherUserPhoto() throws Exception {

        User owner = createUser("realowner");

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", owner);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "bytes".getBytes());

        mockMvc.perform(
                multipart("/photos/{id}", photo.getId())
                        .file(file)
                        .with(request -> { request.setMethod("PUT"); return request; })
                        .with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void replaceFileNullPhoto() throws Exception {

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        mockMvc.perform(
                multipart("/photos/{id}", photo.getId())
                        .with(request -> { request.setMethod("PUT"); return request; })
                        .with(user(user))
        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void updatePhoto() throws Exception {

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        PatchPhotoRequest request = new PatchPhotoRequest("newname");

        mockMvc.perform(
                patch("/photos/{id}", photo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .with(user(user))
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void updateBadRequestPhoto() throws Exception {

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        mockMvc.perform(
                patch("/photos/{id}", photo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filenammme\": \"noname\"}")
                        .with(user(user))
        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void updateNullNamePhoto() throws Exception {

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", user);

        mockMvc.perform(
                patch("/photos/{id}", photo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PatchPhotoRequest(null)))
                        .with(user(user))
        ).andExpect(
                status().isBadRequest()
        );
    }

    @Test
    void updateNonexistentPhoto() throws Exception {

        User user = createUser("tester");

        PatchPhotoRequest request = new PatchPhotoRequest("newname");

        mockMvc.perform(
                patch("/photos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }

    @Test
    void updateOtherUserPhoto() throws Exception {

        User owner = createUser("realowner");

        User user = createUser("tester");
        Photo photo = createPhoto("test.png", owner);

        PatchPhotoRequest request = new PatchPhotoRequest("newname");

        mockMvc.perform(
                patch("/photos/{id}", photo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .with(user(user))
        ).andExpect(
                status().isForbidden()
        );
    }
}
