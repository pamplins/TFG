package com.example.pamplins.apptfg;

import android.widget.ImageView;
import java.util.HashMap;

/**
 * Created by Gustavo on 07/02/2018.
 */

public class User {
    private String uid;
    private String userName;
    private String email;
    private String course;
    private ImageView profileImage;

    public User(){

    }

    public User(String uid, String userName, String email, String course, ImageView img){
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.course = course;
        this.profileImage = img;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public ImageView getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageView profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "User{" +
                " uid='" + uid + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", course='" + course + '\'' +
                '}';
    }

    public HashMap<String, String> toDictionary(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userName", userName);
        hashMap.put("email", email);
        hashMap.put("course", course);
        return hashMap;
    }
}

