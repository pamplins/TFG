package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.DoubtDetailActivity;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

/**
 * Created by Gustavo on 12/03/2018.
 */

//TODO poner codigo en comun de CommentAdapter y DoubtAdapter
public class DoubtAdapter  extends FirebaseRecyclerAdapter<Doubt, DoubtViewHolder> {
    private Activity activity;
    private Controller ctrl;
    private DatabaseReference mDatabase;

    public DoubtAdapter(FirebaseRecyclerOptions<Doubt> options, Activity activity, Controller ctrl, DatabaseReference ref) {
        super(options);
        this.activity = activity;
        this.ctrl = ctrl;
        this.mDatabase = ref;
    }

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
                Intent intent = new Intent(activity, DoubtDetailActivity.class);
                intent.putExtra(Constants.KEY_DOUBT, postKey);
                activity.startActivity(intent);
            }
        });

        checkLikesDis(doubt, viewHolder);
        viewHolder.bindToPost(doubt, activity, ctrl);
        votesDoubt(doubt, viewHolder, postKey);
    }


    private  void votesDoubt(final Doubt doubt, DoubtViewHolder viewHolder, final String postKey){
        viewHolder.bindLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                // Run two transactions
                onLikeClicked(globalPostRef);
                onLikeClicked(userPostRef);
            }
        });

        viewHolder.bindDisLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                // Run two transactions
                onDisLikeClicked(globalPostRef);
                onDisLikeClicked(userPostRef);
            }
        });
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

}
