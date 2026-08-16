package com.projects.phototec;

import com.projects.phototec.model.Album;
import com.projects.phototec.model.Photo;
import com.projects.phototec.model.User;
import com.projects.phototec.repository.AlbumRepository;
import com.projects.phototec.repository.PhotoRepository;
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

//	@Bean
//	public CommandLineRunner commandLineRunner(
//			PhotoRepository photoRepository,
//			AlbumRepository albumRepository,
//			UserService userService
//			) {
//		return args -> {
//			Photo photo = new Photo("minero.jpg", "image/png", new byte[0]);
//			Photo photo2 = new Photo("formal.gif", "image/gif", new byte[0]);
//
//
//			Album album = new Album("Verano");
//			Album album2 = new Album("Japón");
//
//			album.getPhotos().add(photo);
//
//
//			photoRepository.save(photo);
//			albumRepository.save(album);
//			albumRepository.save(album2);
//
//			photoRepository.save(photo);
//			photoRepository.save(photo2);
//		};
//	}
}
