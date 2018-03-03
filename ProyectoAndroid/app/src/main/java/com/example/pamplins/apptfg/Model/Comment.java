package com.example.pamplins.apptfg.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gustavo on 19/02/2018.
 */

@IgnoreExtraProperties
public class Comment { public String uid;
    public String author;
    public String text;

    public int likesCount = 0;
    public int dislikesCount = 0;
    public Map<String, Boolean> likes = new HashMap<>();

    public  String date;

    public Comment() {
    }



    public Comment(String uid, String author, String text, String date) {
        this.uid = uid;
        this.author = author;
        this.text = text;
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

    public Map<String, Boolean> dislikes = new HashMap<>();

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "uid='" + uid + '\'' +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> comment = new HashMap<>();
        comment.put("uid", uid);
        comment.put("author", author);
        comment.put("text", text);
        comment.put("likesCount", likesCount);
        comment.put("disLikesCount", dislikesCount);
        comment.put("date", date);
        comment.put("likes", likes);
        comment.put("dislikes", dislikes);
        return comment;
    }
}

/*
    Ver donde meter esto
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user_images/"+uid+"/image_profile.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                img.buildDrawingCache();
                Bitmap bit = img.getDrawingCache();

                img.setImageBitmap(Utils.getCircularBitmap(bit));
                // Load the image using Glide
                Glide.with(DoubtDetailActivity.this)
                        .load(uri)
                        .into(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(DoubtDetailActivity.this, "ERROOR",
                        Toast.LENGTH_SHORT).show();                    }
        });
 */