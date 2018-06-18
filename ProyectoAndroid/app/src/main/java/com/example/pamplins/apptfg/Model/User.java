package com.example.pamplins.apptfg.Model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gustavo on 07/02/2018.
 */

public class User {
    private String email;
    private String userName;
    private String urlProfileImage;
    private List<String> subjects;
    private List<String> uidDoubts;
    private List<String> uidAnswers;

    public User(){
    }

    public User(String userName, String email, String urlProfileImage){
        this.userName = userName;
        this.email = email;
        this.urlProfileImage = urlProfileImage;
        subjects = new ArrayList<>( Arrays.asList(""));
        uidDoubts = new ArrayList<>( Arrays.asList(""));
        uidAnswers = new ArrayList<>( Arrays.asList(""));
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

    public List<String> getSubjects() {
        return subjects;
    }

    public void addNewSubjects(String subjectName){
        if(subjects.get(0).equals("")){
            subjects.set(0,subjectName);
        }else{
            if(!subjects.contains(subjectName)){
                subjects.add(subjectName);
            }
        }
    }
    public void deleteSubject(String subjectName) {
        subjects.remove(subjectName);
        if(subjects.isEmpty()){
            subjects.add("");
        }
    }
    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public void addNewDoubt(String uid){
        if(getUidDoubts().get(0).equals("")){
            getUidDoubts().set(0,uid);
        }else{
            uidDoubts.add(uid);
        }
    }

    public void addNewAnswer(String uid){
        if(getUidAnswers().get(0).equals("")){
            getUidAnswers().set(0,uid);
        }else{
            uidAnswers.add(uid);
        }
    }

    public List<String> getUidDoubts() {
        return uidDoubts;
    }

    public List<String> getUidAnswers() {
        return uidAnswers;
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

