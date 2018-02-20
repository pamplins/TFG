package com.example.pamplins.apptfg.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gustavo on 19/02/2018.
 */
@IgnoreExtraProperties
public class Post {
    private String uid;
    private String author;
    private String title;
    private String body;
    private int likesCount = 0;
    //private Map<String, Boolean> stars = new HashMap<>();

    public Post() {
    }

    public Post(String uid, String author, String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("likesCount", likesCount);
        return result;
    }
}
