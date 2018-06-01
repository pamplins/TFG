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
import com.example.pamplins.apptfg.HoldersAdapters.DoubtViewHolder;
import com.example.pamplins.apptfg.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class HomeFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Doubt, DoubtViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private Controller ctrl;
    private ProgressBar progressBar;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ctrl = Controller.getInstance();
        mDatabase = ctrl.getDB().getReference();
        mRecycler = rootView.findViewById(R.id.messages_list);
        progressBar = rootView.findViewById(R.id.progressBar_h);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showDoubts();
    }

    /**
     * Metodo encargado de especificar el recycleview y configurar el adapter en optionns
     */
    public void showDoubts() {
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // si quiero limitar los comentarios a mostrar en home poner mDatabase.limitToFirst(X)
        final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Doubt>()
                .setQuery(mDatabase.child(Constants.REF_DOUBTS), Doubt.class)
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
        mAdapter.startListening();
    }
}
