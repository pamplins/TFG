package com.example.pamplins.apptfg.Model;

/**
 * Created by Gustavo on 25/04/2018.
 */

public class Subject {

    private String name;
    private String course;
    private String semester;

    public Subject(String name, String course, String semester){
        this.name = name;
        this.course = course;
        this.semester = semester;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                ", course='" + course + '\'' +
                ", semester='" + semester + '\'' +
                '}';
    }
}
