package com.example.pamplins.apptfg.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.DoubtDetailActivity;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.DoubtViewHolder;
import com.example.pamplins.apptfg.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class HomeFragment extends Fragment {

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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ctrl = Controller.getInstance();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showDoubtsHome();
    }

    private void showDoubtsHome() {

        mRecycler = getActivity().findViewById(R.id.messages_list);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        // si quiero limitar los comentarios a mostrar en home poner mDatabase.limitToFirst(X)
        final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Doubt>()
                .setQuery(mDatabase.child("doubts"), Doubt.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Doubt, DoubtViewHolder>(options) {

            @Override
            public DoubtViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_doubt, viewGroup, false);
                return new DoubtViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(final DoubtViewHolder viewHolder, int position, final Doubt doubt) {
                final DatabaseReference postRef = getRef(viewHolder.getAdapterPosition());
                final String postKey = postRef.getKey();

                // Abrir una duda X de la lista de home
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), DoubtDetailActivity.class);
                        intent.putExtra(DoubtDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                checkLikesDis(doubt, viewHolder);

                viewHolder.bindToPost(doubt, getActivity(), ctrl);

                // Poner los valores en la caja de duda de home
                viewHolder.bindLikes(doubt, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference globalPostRef = mDatabase.child("doubts").child(postKey);
                        DatabaseReference userPostRef = mDatabase.child("user_doubts").child(doubt.getUid()).child(postKey);
                        // Run two transactions
                        onLikeClicked(globalPostRef);
                        onLikeClicked(userPostRef);
                    }
                });

                viewHolder.bindDisLikes(doubt, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference globalPostRef = mDatabase.child("doubts").child(postKey);
                        DatabaseReference userPostRef = mDatabase.child("user_doubts").child(doubt.getUid()).child(postKey);
                        // Run two transactions
                        onDisLikeClicked(globalPostRef);
                        onDisLikeClicked(userPostRef);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.startListening();
    }

    private void checkLikesDis(Doubt doubt, DoubtViewHolder viewHolder) {
        // TODO si le da like y el dislike estaba ya activo. qitar dislike (restando de la mapHash) y activar like (+1 hashMap)
        if (doubt.getLikes().containsKey(ctrl.getUid())) {
            viewHolder.getLike().setImageResource(R.drawable.like_ac);
        } else {
            viewHolder.getLike().setImageResource(R.drawable.like);
        }
        if (doubt.getDislikes().containsKey(ctrl.getUid())) {
            viewHolder.getDislike().setImageResource(R.drawable.dislike_ac);
        } else {
            viewHolder.getDislike().setImageResource(R.drawable.dislike);
        }
    }

    private void onLikeClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Doubt doubt = mutableData.getValue(Doubt.class);
                if (doubt == null) {
                    return Transaction.success(mutableData);
                }
                if (doubt.getLikes().containsKey(ctrl.getUid())) {
                    // Unstar the post and remove self from stars
                    doubt.setLikesCount(doubt.getLikesCount() - 1);
                    doubt.getLikes().remove(ctrl.getUid());
                } else {
                    // Star the post and add self to stars
                    doubt.setLikesCount(doubt.getLikesCount() + 1);
                    doubt.getLikes().put(ctrl.getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(doubt);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    private void onDisLikeClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Doubt doubt = mutableData.getValue(Doubt.class);
                if (doubt == null) {
                    return Transaction.success(mutableData);
                }
                if (doubt.getDislikes().containsKey(ctrl.getUid())) {
                    // Unstar the post and remove self from stars
                    doubt.setDislikesCount(doubt.getDislikesCount() - 1);
                    doubt.getDislikes().remove(ctrl.getUid());
                } else {
                    // Star the post and add self to stars
                    doubt.setDislikesCount(doubt.getDislikesCount() + 1);
                    doubt.getDislikes().put(ctrl.getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(doubt);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            mAdapter.startListening();
            mRecycler.setAdapter(mAdapter);
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
