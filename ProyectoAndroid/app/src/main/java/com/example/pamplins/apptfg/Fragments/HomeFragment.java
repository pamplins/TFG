package com.example.pamplins.apptfg.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.R;
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
        return rootView;
    }
}
