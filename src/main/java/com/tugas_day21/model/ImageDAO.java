package com.tugas_day21.model;

import javax.persistence.*;

@Entity
@Table(name = "movie_image")
public class ImageDAO {
    @Id
    private String id_imdb;

    @Column
    private String image;

    public ImageDAO() {
    }

    public ImageDAO(String id_imdb, String image) {
        this.id_imdb = id_imdb;
        this.image = image;
    }

    public String getId_imdb() {
        return id_imdb;
    }

    public void setId_imdb(String id_imdb) {
        this.id_imdb = id_imdb;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
