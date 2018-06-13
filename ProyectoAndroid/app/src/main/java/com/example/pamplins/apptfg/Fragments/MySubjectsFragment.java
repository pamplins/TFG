package com.example.pamplins.apptfg.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.SubjectAdapter;
import com.example.pamplins.apptfg.Model.Subject;
import com.example.pamplins.apptfg.Utils;
import com.example.pamplins.apptfg.View.CoursesActivity;
import com.example.pamplins.apptfg.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class MySubjectsFragment extends Fragment {
    private RecyclerView mRecycler;
    private Controller ctrl;
    private ProgressBar progressBar;
    private ImageView addNewCourse;
    private SubjectAdapter mAdapter;
    private String subjects;
    private HashMap<String, Subject> hashMap;
    private TextView emptySubjects;
    private List listSubjects;
    private List listKeys;

    public MySubjectsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_subjects, container, false);
        mRecycler = rootView.findViewById(R.id.messages_list_s);
        progressBar = rootView.findViewById(R.id.progressBar_s);
        addNewCourse = rootView.findViewById(R.id.tv_add_course);
        hashMap = new HashMap<>();
        emptySubjects = rootView.findViewById(R.id.tv_empty_subjects);
        ctrl = Controller.getInstance();

        addNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(Utils.getButtonAnimation());
                Intent i = new Intent(getActivity().getApplicationContext(), CoursesActivity.class);
                startActivity(i);
            }
        });
        emptySubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(Utils.getButtonAnimation());
                Intent i = new Intent(getActivity().getApplicationContext(), CoursesActivity.class);
                startActivity(i);
            }
        });

        if(getArguments() != null){
            subjects = getArguments().getString("subjects");
            if(null != subjects) {
                addNewCourse(subjects);
                getArguments().remove("subjects");
            }
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showSubjects();

    }

    private void addNewCourse(String subjects) {
        ctrl.updateUserSubjects(subjects, true);
    }


    public void showSubjects() {
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        listSubjects =  new ArrayList<>();
        listKeys = new ArrayList<>();
        int i = 0;

        try {
            for (String key : ctrl.getUser().getSubjects()) {
                i++;
                listKeys.add(key);
                final int finalI = i;
                ctrl.getSubjectsRef().child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Subject subject = dataSnapshot.getValue(Subject.class);
                        listSubjects.add(subject);
                        if (finalI == ctrl.getUser().getSubjects().size()) {
                            if(ctrl.getUser().getSubjects().contains("")){
                                emptySubjects.setVisibility(View.VISIBLE);
                            }else{
                                progressBar.setVisibility(View.GONE);
                                mRecycler.setVisibility(View.VISIBLE);
                                emptySubjects.setVisibility(View.GONE);
                                mAdapter =  new SubjectAdapter(listSubjects, listKeys, getActivity(), ctrl);
                                mRecycler.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }catch (Exception e){
       }

    }
}

