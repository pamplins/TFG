package com.example.pamplins.apptfg.Model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gustavo on 19/02/2018.
 */
public class Doubt {
    private User user;
    private String uid;
    private String title;
    private String description;
    private int likesCount = 0;
    private int dislikesCount = 0;

    private String date;
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, Boolean> dislikes = new HashMap<>();
    private long nAnswers;
    private ArrayList<String> urlsImages;

    public Doubt() {
    }

    public Doubt(String uid, String title, String description, String date, User user, ArrayList<String> urlsImages) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.date = date;
        this.user = user;
        this.nAnswers = 0;
        this.urlsImages = urlsImages;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getnAnswers() {
        return nAnswers;
    }

    public void setnAnswers(long nAnswers) {
        this.nAnswers = nAnswers;
    }

    public ArrayList<String> getUrlsImages() {
        return urlsImages;
    }

    public void setUrlsImages(ArrayList<String> urlsImages) {
        this.urlsImages = urlsImages;
    }

    @Override
    public String toString() {
        return "Doubt{" +
                "user=" + user +
                ", uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", likesCount=" + likesCount +
                ", dislikesCount=" + dislikesCount +
                ", date='" + date + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", nAnswers=" + nAnswers +
                ", urlImage='" + urlsImages + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> doubt = new HashMap<>();
        doubt.put("uid", uid);
        doubt.put("title", title);
        doubt.put("description", description);
        doubt.put("likesCount", likesCount);
        doubt.put("disLikesCount", dislikesCount);
        doubt.put("date", date);
        doubt.put("likes", likes);
        doubt.put("dislikes", dislikes);
        doubt.put("nAnswers", nAnswers);
        doubt.put("user", user);
        doubt.put("urlsImages", urlsImages);
        return doubt;
    }
}
