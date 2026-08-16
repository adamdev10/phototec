package com.projects.phototec.model;

import jakarta.persistence.*;
import org.hibernate.dialect.type.PgJdbcHelper;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Album {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "album_sequence"
    )
    @SequenceGenerator(
            name = "album_sequence",
            sequenceName = "album_sequence",
            allocationSize = 1
    )
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;


    @ManyToMany
    @JoinTable(
            name = "albums_photos",
            joinColumns = {
                    @JoinColumn(name = "album_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "photo_id")
            },
            uniqueConstraints = {
                    @UniqueConstraint(
                            columnNames = {"album_id", "photo_id"}
                    )
            }
    )
    private List<Photo> photos;

    public Album() {

    }

    public Album(String name, User owner) {
        this.name = name;
        this.photos = new ArrayList<>();
        this.owner = owner;
    }

    public Album(String name, List<Photo> photos, User owner) {
        this.photos = photos;
        this.name = name;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
        photo.getAlbums().add(this);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
        photo.getAlbums().remove(this);
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                ", photos=" + photos +
                '}';
    }
}
