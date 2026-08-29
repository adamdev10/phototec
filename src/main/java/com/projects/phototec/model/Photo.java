package com.projects.phototec.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Photo {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "photo_sequence"
    )
    @SequenceGenerator(
            name = "photo_sequence",
            sequenceName = "photo_sequence",
            allocationSize = 1
    )
    private Long id;

    private String fileName;

    private String contentType;

    private String key;

    @ManyToMany(mappedBy = "photos")
    private List<Album> albums;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;

    public Photo() {

    }

    public Photo(String fileName, String contentType, String key, User owner) {
        this.key = key;
        this.contentType = contentType;
        this.fileName = fileName;
        this.owner = owner;
        this.albums = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", key=" + key +
                ", albums=" + albums +
                '}';
    }
}
