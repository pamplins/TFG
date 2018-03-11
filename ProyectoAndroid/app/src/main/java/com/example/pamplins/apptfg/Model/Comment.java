package com.example.pamplins.apptfg.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gustavo on 19/02/2018.
 */

@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String text;
    private int likesCount = 0;
    private int dislikesCount = 0;
    private Map<String, Boolean> likes = new HashMap<>();
    private Map<String, Boolean> dislikes = new HashMap<>();
    private String date;
    private User user;

    public Comment() {
    }

    public Comment(String uid,String text, String date, User user) {
        this.uid = uid;
        this.text = text;
        this.date = date;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "uid='" + uid + '\'' +
                ", text='" + text + '\'' +
                ", likesCount=" + likesCount +
                ", dislikesCount=" + dislikesCount +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", date='" + date + '\'' +
                ", user=" + user +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> comment = new HashMap<>();
        comment.put("uid", uid);
        comment.put("text", text);
        comment.put("likesCount", likesCount);
        comment.put("disLikesCount", dislikesCount);
        comment.put("date", date);
        comment.put("likes", likes);
        comment.put("dislikes", dislikes);
        comment.put("user", user);
        return comment;
    }
}