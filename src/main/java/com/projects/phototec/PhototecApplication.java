package com.projects.phototec;

import com.projects.phototec.model.Album;
import com.projects.phototec.model.Photo;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.AlbumRepository;
import com.projects.phototec.repository.PhotoRepository;
import com.projects.phototec.repository.UserRepository;
import com.projects.phototec.security.Role;
import com.projects.phototec.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PhototecApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhototecApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			PhotoRepository photoRepository,
			AlbumRepository albumRepository,
			UserService userService,
			UserRepository userRepository
			) {
		return args -> {

			userService.register("Lukaku", "bomdia");
			userService.register("usere", "bomdia");
			userService.register("Boss", "bomdia");

			User user1 = userService.getUser(1L);
			User user2 = userService.getUser(2L);
			User admin = userService.getUser(3L);
			admin.setRole(Role.ADMIN);
			userRepository.save(admin);


			Photo photo = new Photo("minero.jpg", "image/png", new byte[0], user1);
			Photo photo2 = new Photo("formal.gif", "image/gif", new byte[0], user2);


			Album album = new Album("Japan", user1);
			Album album2 = new Album("Italy", user2);

			album.getPhotos().add(photo);


			photoRepository.save(photo);
			albumRepository.save(album);
			albumRepository.save(album2);

			photoRepository.save(photo);
			photoRepository.save(photo2);
		};
	}
}
