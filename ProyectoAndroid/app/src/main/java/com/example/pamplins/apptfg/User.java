package com.example.pamplins.apptfg;


/**
 * Created by Gustavo on 07/02/2018.
 */

public class User {
    private String uid;
    private String email;
    private String course;

    public User(){
    }

    public User(String uid, String email, String course){
        this.uid = uid;
        this.email = email;
        this.course = course;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    @Override
    public String toString() {
        return "User{" +
                " uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", course='" + course + '\'' +
                '}';
    }
}

