package com.example.pamplins.apptfg.Model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Gustavo on 19/02/2018.
 */

@IgnoreExtraProperties
public class Comment { public String uid;
    public String author;
    public String text;
    //private int likesCount; // determinara la mejor respuesta a mas votos
    //private int dislikeCount;
    public Comment() {
    }

    public Comment(String uid, String author, String text) {
        this.uid = uid;
        this.author = author;
        this.text = text;
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

    @Override
    public String toString() {
        return "Comment{" +
                "uid='" + uid + '\'' +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                '}';
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