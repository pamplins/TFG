package com.example.pamplins.apptfg.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.DoubtAdapter;
import com.example.pamplins.apptfg.HoldersAdapters.DoubtViewHolder;
import com.example.pamplins.apptfg.View.MainActivity;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.View.Courses;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.View.Login;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class MySubjectsFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Doubt, DoubtViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private Controller ctrl;
    private ProgressBar progressBar;
    private ImageView addNewCourse;
    public MySubjectsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_subjects, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ctrl = Controller.getInstance();
        mRecycler = rootView.findViewById(R.id.messages_list_s);
        progressBar = rootView.findViewById(R.id.progressBar_s);
        addNewCourse = rootView.findViewById(R.id.tv_add_course);
        addNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), Courses.class);
                startActivity(i);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showDoubts();
    }

    public void addNewCourse(final View v) {


    }

    public void showDoubts() {
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // si quiero limitar los comentarios a mostrar en home poner mDatabase.limitToFirst(X)
        final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Doubt>()
                .setQuery(mDatabase.child(Constants.REF_COURSE+"/first/second_semester/elect"), Doubt.class)
                .build();
        setDoubtAdapter(options);
    }

    private void setDoubtAdapter(FirebaseRecyclerOptions options){
        mAdapter = new DoubtAdapter(options, getActivity(), ctrl, mDatabase);
        mRecycler.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if(mAdapter.getItemCount() > 0 ){
                    progressBar.setVisibility(View.GONE);
                    mRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onStart() {
        super.onStart();
       // mAdapter.startListening();
    }
}