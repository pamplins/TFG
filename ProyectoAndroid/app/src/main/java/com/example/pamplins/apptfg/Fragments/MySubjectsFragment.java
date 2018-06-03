package com.example.pamplins.apptfg.Fragments;

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

    public MySubjectsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctrl = Controller.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_subjects, container, false);
        mRecycler = rootView.findViewById(R.id.messages_list_s);
        progressBar = rootView.findViewById(R.id.progressBar_s);
        addNewCourse = rootView.findViewById(R.id.tv_add_course);
        hashMap = new HashMap<>();
        emptySubjects = rootView.findViewById(R.id.tv_empty_subjects);

        addNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), CoursesActivity.class);
                startActivity(i);
            }
        });
        emptySubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        ctrl.getSubjectsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subject subject = snapshot.getValue(Subject.class);
                        if (ctrl.getUser().getSubjects().contains(snapshot.getKey())) {
                            if (!hashMap.keySet().contains(snapshot.getKey())) {
                                hashMap.put(snapshot.getKey(), subject);
                                keys.add(snapshot.getKey());

                            } else {
                                hashMap.put(snapshot.getKey(), subject);
                            }
                        }
                }
                List listSubjects = new ArrayList<>(hashMap.values());
                List listKeys = new ArrayList<>(hashMap.keySet());
                progressBar.setVisibility(View.GONE);

                if(listKeys.isEmpty()){
                    emptySubjects.setVisibility(View.VISIBLE);
                }else {
                    mAdapter = new SubjectAdapter(listSubjects, listKeys, getActivity(), ctrl);
                    mRecycler.setVisibility(View.VISIBLE);
                    mRecycler.setAdapter(mAdapter);
                    emptySubjects.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }
}
