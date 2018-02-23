package com.example.pamplins.apptfg.Model;


/**
 * Created by Gustavo on 07/02/2018.
 */

public class User {
    private String email;
    private String course;
    private String userName;

    public User(){
    }



    public User(String userName, String email, String course){
        this.userName = userName;
        this.email = email;
        this.course = course;
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

    @Override
    public String toString() {
        return "User{" +
                " userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", course='" + course + '\'' +
                '}';
    }
}

