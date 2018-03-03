package com.example.pamplins.apptfg.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gustavo on 19/02/2018.
 */
public class Doubt {
    private String uid;
    private String author;
    private String title;
    private String description;
    private int likesCount = 0;
    private int dislikesCount = 0;

    private String date;
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, Boolean> dislikes = new HashMap<>();


    //private String course;
    //private int dislikesCount;
    //private Map<String, Boolean> stars = new HashMap<>();

    public Doubt() {
    }


    public Doubt(String uid, String author, String title, String description, String date) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.description = description;
        this.date = date;
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

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public Map<String, Boolean> getDislikes() {
        return dislikes;
    }

    public void setDislikes(Map<String, Boolean> dislikes) {
        this.dislikes = dislikes;
    }

    @Override
    public String toString() {
        return "Doubt{" +
                "uid='" + uid + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", likesCount=" + likesCount +
                ", date='" + date + '\'' +
                ", likes=" + likes +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> doubt = new HashMap<>();
        doubt.put("uid", uid);
        doubt.put("author", author);
        doubt.put("title", title);
        doubt.put("description", description);
        doubt.put("likesCount", likesCount);
        doubt.put("disLikesCount", dislikesCount);
        doubt.put("date", date);
        doubt.put("likes", likes);
        doubt.put("dislikes", dislikes);
        return doubt;
    }
}
