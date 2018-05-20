package com.example.pamplins.apptfg.Model;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Gustavo on 07/02/2018.
 */

public class User {
    private String email;
    private String userName;
    private String urlProfileImage;
    private ArrayList<String> subjects;

    public User(){
    }

    public User(String userName, String email, String urlProfileImage){
        this.userName = userName;
        this.email = email;
        this.urlProfileImage = urlProfileImage;
        subjects = new ArrayList<>( Arrays.asList(""));
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

    public String getUrlProfileImage() {
        return urlProfileImage;
    }

    public void setUrlProfileImage(String urlProfileImage) {
        this.urlProfileImage = urlProfileImage;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void addNewSubjects(String subjectsName){
        subjects.add(subjectsName);
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", urlProfileImage='" + urlProfileImage + '\'' +
                ", subjects ='" + subjects + '\'' +
                '}';
    }
}

