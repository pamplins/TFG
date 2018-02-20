package com.example.pamplins.apptfg.Model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Gustavo on 19/02/2018.
 */

@IgnoreExtraProperties
public class Comment { public String uid;
    public String author;
    public String text;

    public Comment() {
    }

    public Comment(String uid, String author, String text) {
        this.uid = uid;
        this.author = author;
        this.text = text;
    }
}
