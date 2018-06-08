package com.example.pamplins.apptfg.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.DoubtAdapter;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class HomeFragment extends DoubtsFragment {

    public HomeFragment() {

    }

    @Override
    public Query getQuery() {
        return mDatabase.child(Constants.REF_DOUBTS);
    }

    @Override
    public View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mRecycler = rootView.findViewById(R.id.messages_list);
        progressBar = rootView.findViewById(R.id.progressBar_h);
        prueba = rootView.findViewById(R.id.tv_empty_doubts_p);
        return rootView;
    }


}
