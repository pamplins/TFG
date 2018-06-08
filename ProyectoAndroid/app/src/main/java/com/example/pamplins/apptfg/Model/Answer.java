package com.example.pamplins.apptfg.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gustavo on 19/02/2018.
 */

@IgnoreExtraProperties
public class Answer{

    public String uid;
    public String text;
    private int likesCount = 0;
    private int dislikesCount = 0;
    private Map<String, Boolean> likes = new HashMap<>();
    private Map<String, Boolean> dislikes = new HashMap<>();
    private String date;
    private String author;
    private String urlProfileImage;
    private List<String> urlsImages;

    public Answer() {
    }

    public Answer(String uid, String text, String date, String author, String urlProfileImage, List<String> urlsImages) {
        this.uid = uid;
        this.text = text;
        this.date = date;
        this.author = author;
        this.urlProfileImage = urlProfileImage;
        this.urlsImages = urlsImages;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, Boolean> getDislikes() {
        return dislikes;
    }

    public void setDislikes(Map<String, Boolean> dislikes) {
        this.dislikes = dislikes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    @Override
    public String toString() {
        return "Answer{" +
                "uid='" + uid + '\'' +
                ", text='" + text + '\'' +
                ", likesCount=" + likesCount + '\'' +
                ", dislikesCount=" + dislikesCount +  '\'' +
                ", likes=" + likes + '\'' +
                ", dislikes=" + dislikes + '\'' +
                ", date='" + date + '\'' +
                ", author=" + author + '\'' +
                ", urlProfileImage=" + urlProfileImage + '\'' +
                ", urlsImages=" + urlsImages +

                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> answer = new HashMap<>();
        answer.put("uid", uid);
        answer.put("text", text);
        answer.put("likesCount", likesCount);
        answer.put("disLikesCount", dislikesCount);
        answer.put("date", date);
        answer.put("likes", likes);
        answer.put("dislikes", dislikes);
        answer.put("author", author);
        answer.put("urlProfileImage", urlProfileImage);
        answer.put("urlsImages", urlsImages);
        return answer;
    }

}