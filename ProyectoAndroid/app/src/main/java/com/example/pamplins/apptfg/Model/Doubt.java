package com.example.pamplins.apptfg.Model;

/**
 * Created by Gustavo on 19/02/2018.
 */
public class Doubt {
    private String uid;
    private String author;
    private String title;
    private String description;
    private int likesCount = 0;
    private String date;

    private String urlImagePerfil;
    //private String course;
    //private int dislikesCount;
    //private Map<String, Boolean> stars = new HashMap<>();

    public Doubt() {
    }

    public Doubt(String uid, String author, String title, String description, String date, String urlImagePerfil) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.description = description;
        this.date = date;
        this.urlImagePerfil = urlImagePerfil;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrlImagePerfil() {
        return urlImagePerfil;
    }

    public void setUrlImagePerfil(String urlImagePerfil) {
        this.urlImagePerfil = urlImagePerfil;
    }

    @Override
    public String toString() {
        return "Doubt{" +
                "uid='" + uid + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", likesCount=" + likesCount +
                '}';
    }
}
