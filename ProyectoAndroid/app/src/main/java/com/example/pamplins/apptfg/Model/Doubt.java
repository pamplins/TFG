package com.example.pamplins.apptfg.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gustavo on 19/02/2018.
 */
public class Doubt {
    private String uid;
    private String author;
    private String urlProfileImage;
    private String title;
    private String description;
    private int likesCount = 0;
    private int dislikesCount = 0;
    private String date;
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, Boolean> dislikes = new HashMap<>();
    private long nAnswers;
    private List<String> urlsImages;
    private String subject;

    public Doubt() {
    }

    public Doubt(String uid, String title, String description, String date, String author, String urlProfileImage, List<String> urlsImages, String subject) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.date = date;
        this.author = author;
        this.urlProfileImage = urlProfileImage;
        this.nAnswers = 0;
        this.urlsImages = urlsImages;
        this.subject = subject;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrlProfileImage() {
        return urlProfileImage;
    }

    public void setUrlProfileImage(String urlProfileImage) {
        this.urlProfileImage = urlProfileImage;
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


    public long getnAnswers() {
        return nAnswers;
    }

    public void setnAnswers(long nAnswers) {
        this.nAnswers = nAnswers;
    }

    public List<String> getUrlsImages() {
        return urlsImages;
    }

    public void setUrlsImages(List<String> urlsImages) {
        this.urlsImages = urlsImages;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Doubt{" +
                "author=" + author + '\'' +
                ", urlProfileImage='" + urlProfileImage + '\'' +
                ", uid='" + uid + '\'' +
                ", subject='" + subject + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", likesCount=" + likesCount +
                ", dislikesCount=" + dislikesCount +
                ", date='" + date + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes + '\'' +
                ", nAnswers=" + nAnswers + '\'' +
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
        doubt.put("author", author);
        doubt.put("urlProfileImage", urlProfileImage);
        doubt.put("urlsImages", urlsImages);
        doubt.put("subject", subject);
        return doubt;
    }
}
