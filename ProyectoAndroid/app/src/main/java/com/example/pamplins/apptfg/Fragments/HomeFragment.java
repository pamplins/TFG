package com.example.pamplins.apptfg.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.DoubtDetailActivity;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.DoubtViewHolder;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;
import com.example.pamplins.apptfg.View.Login;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "PostListFragment";

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Doubt, DoubtViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private Controller ctrl;

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

        mDatabase = FirebaseDatabase.getInstance().getReference("doubts");

        mRecycler = rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);
        ctrl = Controller.getInstance();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // si quiero limitar los comentarios a mostrar en home poner mDatabase.limitToFirst(X)
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Doubt>()
                .setQuery(mDatabase, Doubt.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Doubt, DoubtViewHolder>(options) {

            @Override
            public DoubtViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_doubt, viewGroup, false);
                return new DoubtViewHolder(v);
            }

            // Abrir una duda X de la lista de home
            @Override
            protected void onBindViewHolder(DoubtViewHolder viewHolder, int position, final Doubt model) {
                final DatabaseReference postRef = getRef(position);
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), DoubtDetailActivity.class);
                        intent.putExtra(DoubtDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly
                /*if (model.getLikesCount().containsKey(ctrl.getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }*/

                // Poner los valores en la caja de duda de home
               viewHolder.bindToPost(model, getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("doubts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user_doubts").child(model.getUid()).child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Doubt doubt = mutableData.getValue(Doubt.class);
                if (doubt == null) {
                    return Transaction.success(mutableData);
                }
                /*
                if (doubt.getLikesCount().containsKey(ctrl.getUid())) {
                    // Unstar the post and remove self from stars
                    doubt.starCount = doubt.starCount - 1;
                    doubt.stars.remove(ctrl.getUid());
                } else {
                    // Star the post and add self to stars
                    doubt.starCount = doubt.starCount + 1;
                    doubt.stars.put(ctrl.getUid(), true);
                }*/

                // Set value and report transaction success
                mutableData.setValue(doubt);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}
