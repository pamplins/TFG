package com.example.pamplins.apptfg.Model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Gustavo on 25/04/2018.
 */

public class Subject {

    private String course;
    private String semester;
    private ArrayList<String> doubts;


    public Subject(){}

    public Subject(String course, String semester){
        this.course = course;
        this.semester = semester;
        doubts = new ArrayList<>( Arrays.asList(""));
    }


    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public ArrayList<String> getDoubts() {
        return doubts;
    }

    public void setDoubts(ArrayList<String> doubts) {
        this.doubts = doubts;
    }

    @Override
    public String toString() {
        return "Subject{" +
                ", course='" + course + '\'' +
                ", semester='" + semester + '\'' +
                ", doubts ='" + doubts + '\'' +
                '}';
    }
}
