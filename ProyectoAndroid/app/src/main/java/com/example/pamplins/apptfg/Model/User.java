package com.example.pamplins.apptfg.Model;


/**
 * Created by Gustavo on 07/02/2018.
 */

public class User {
    private String email;
    private String course;
    private String userName;
    private String urlProfileImage;

    public User(){
    }



    public User(String userName, String email, String course, String urlProfileImage){
        this.userName = userName;
        this.email = email;
        this.course = course;
        this.urlProfileImage = urlProfileImage;
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

    public String getUrlProfileImage() {
        return urlProfileImage;
    }

    public void setUrlProfileImage(String urlProfileImage) {
        this.urlProfileImage = urlProfileImage;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", course='" + course + '\'' +
                ", userName='" + userName + '\'' +
                ", urlProfileImage='" + urlProfileImage + '\'' +
                '}';
    }
}

