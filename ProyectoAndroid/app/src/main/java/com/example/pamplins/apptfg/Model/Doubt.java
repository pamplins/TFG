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
    //private Map<String, Boolean> stars = new HashMap<>();

    public Doubt() {
    }

    public Doubt(String uid, String author, String title, String description) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.description = description;
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
