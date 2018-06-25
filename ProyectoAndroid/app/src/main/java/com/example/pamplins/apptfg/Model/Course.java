package com.example.pamplins.apptfg.Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class Course {

    private HashMap<String, ArrayList<String>> subjects;
    public Course(){}

    public Course(HashMap<String, ArrayList<String>> subjects){
        this.subjects = subjects;
    }

    public HashMap<String, ArrayList<String>> getSubjects() {
        return subjects;
    }

    public void setSubjects(HashMap<String, ArrayList<String>> subjects) {
        this.subjects = subjects;
    }


    @Override
    public String toString() {
        return "Course{" +
                "subjects=" + subjects +
                '}';
    }
}
