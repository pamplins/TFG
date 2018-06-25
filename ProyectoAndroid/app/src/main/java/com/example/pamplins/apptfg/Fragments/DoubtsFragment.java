package com.example.pamplins.apptfg.Fragments;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.DoubtAdapter;
import com.example.pamplins.apptfg.Model.Doubt;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Gustavo on 04/06/2018.
 */

public abstract class DoubtsFragment extends Fragment {

    protected DatabaseReference mDatabase;
    protected FirebaseRecyclerAdapter<Doubt, DoubtAdapter.DoubtViewHolder> mAdapter;
    protected RecyclerView mRecycler;
    protected LinearLayoutManager mManager;
    protected Controller ctrl;
    protected ProgressBar progressBar;

    public DoubtsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctrl = Controller.getInstance();
        mDatabase = ctrl.getDB().getReference();
        return setView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showDoubts();
    }

    /**
     * Metodo encargado de especificar el recycleview y configurar el adapter en optionns
     */
    private void showDoubts() {
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        Query query = getQuery();
        final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Doubt>()
                .setQuery(query, Doubt.class)
                .build();
        setDoubtAdapter(options);
    }

    /**
     * Metodo encargado de mostrar el adapter que contiene tods las dudas
     * dentro del recycleview
     *
     * @param options
     */
    private void setDoubtAdapter(FirebaseRecyclerOptions options){
        mAdapter = new DoubtAdapter(options, getActivity(), ctrl, mDatabase);
        mRecycler.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if(mAdapter.getItemCount() > 0 ){
                    //progressBar.setVisibility(View.GONE);
                    mRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    public abstract Query getQuery();

    public abstract View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

}
