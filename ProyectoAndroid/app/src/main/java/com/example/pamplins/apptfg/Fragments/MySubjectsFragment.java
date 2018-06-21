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
import java.util.Map;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class MySubjectsFragment extends Fragment {
    private RecyclerView mRecycler;
    private Controller ctrl;
    private ImageView addNewCourse;
    private SubjectAdapter mAdapter;
    private String subjects;
    private TextView emptySubjects;

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
        addNewCourse = rootView.findViewById(R.id.tv_add_course);
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

    /**
     * Metodo encargado de mostrar para el usuario actual todas sus asignaturass
     */
    public void showSubjects() {
        final Map<String, Subject> listSubjects = new HashMap<>();
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        if (ctrl.getUser().getSubjects().contains("")) {
            emptySubjects.setVisibility(View.VISIBLE);
        }else {
            for (String key : ctrl.getUser().getSubjects()) {
                ctrl.getSubjectsRef().child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Subject subject = dataSnapshot.getValue(Subject.class);
                        if (null != subject.getCourse()) {
                            if (!listSubjects.keySet().contains(dataSnapshot.getKey())) {
                                listSubjects.put(dataSnapshot.getKey(), subject);
                            } else {
                                listSubjects.put(dataSnapshot.getKey(), subject);
                            }
                        }
                        if (ctrl.getUser().getSubjects().contains("")) {
                            emptySubjects.setVisibility(View.VISIBLE);
                        } else {
                            if (listSubjects.keySet().size() == ctrl.getUser().getSubjects().size()) {
                                mRecycler.setVisibility(View.VISIBLE);
                                emptySubjects.setVisibility(View.GONE);
                                List listDoubts = new ArrayList<>(listSubjects.values());
                                List doubtNames = new ArrayList<>(listSubjects.keySet());
                                mAdapter = new SubjectAdapter(listDoubts, doubtNames, getActivity(), ctrl);
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
        }
    }
}

