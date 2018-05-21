package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.Subject;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.View.DoubtDetailActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.List;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class DoubtAdapterAct extends RecyclerView.Adapter<DoubtViewHolder> {
    private List<Doubt> doubts;
    private List<String> keys;
    private Activity activity;
    private Controller ctrl;

    public DoubtAdapterAct(List<Doubt> doubts, List<String> keys, Activity activity) {
        super();
        this.doubts = doubts;
        this.keys = keys;
        this.activity = activity;
        ctrl = Controller.getInstance();
    }


    @Override
    public DoubtViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_doubt, viewGroup, false);
        return new DoubtViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DoubtViewHolder holder, final int position) {
        Doubt doubt = doubts.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DoubtDetailActivity.class);
                intent.putExtra(Constants.KEY_DOUBT, keys.get(position));
                activity.startActivity(intent);
            }
        });
        checkLikesDis(doubt, holder);
        holder.bindToPost(doubt, activity, ctrl);
        votesDoubt(doubt, holder, keys.get(position));

    }


    private  void votesDoubt(final Doubt doubt, final DoubtViewHolder viewHolder, final String postKey) {
        viewHolder.bindLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = FirebaseDatabase.getInstance().getReference().child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                onLikeClicked(globalPostRef, true);
                onLikeClicked(userPostRef, true);
                notifyDataSetChanged();
            }
        });

        viewHolder.bindDisLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = FirebaseDatabase.getInstance().getReference().child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                onDisLikeClicked(globalPostRef, true);
                onDisLikeClicked(userPostRef, true);
                notifyDataSetChanged();
            }
        });
    }


    private void checkLikesDis(Doubt doubt, DoubtViewHolder viewHolder) {
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

    private void onLikeClicked(final DatabaseReference postRef, final boolean checkDisLike) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Doubt doubt = mutableData.getValue(Doubt.class);
                if (doubt == null) {
                    return Transaction.success(mutableData);
                }
                if (doubt.getLikes().containsKey(ctrl.getUid())) {
                    // si la duda tiene like se la quitamos
                    doubt.setLikesCount(doubt.getLikesCount() - 1);
                    doubt.getLikes().remove(ctrl.getUid());
                } else {
                    doubt.setLikesCount(doubt.getLikesCount() + 1);
                    doubt.getLikes().put(ctrl.getUid(), true);
                }

                mutableData.setValue(doubt);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if(checkDisLike){ //comprobaremos si tiene dislike para asi quitarlo o ponerlo. Enviamos falso para no comprobar de nuevo el like. solo Dislike
                    Doubt doubt = dataSnapshot.getValue(Doubt.class);
                    if(doubt.getDislikes().containsKey(ctrl.getUid())){
                        onDisLikeClicked(postRef, false);
                    }
                }
            }
        });
    }

    private void onDisLikeClicked(final DatabaseReference postRef, final boolean checkLike) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Doubt doubt = mutableData.getValue(Doubt.class);
                if (doubt == null) {
                    return Transaction.success(mutableData);
                }
                if (doubt.getDislikes().containsKey(ctrl.getUid())) {
                    doubt.setDislikesCount(doubt.getDislikesCount() - 1);
                    doubt.getDislikes().remove(ctrl.getUid());
                } else {
                    doubt.setDislikesCount(doubt.getDislikesCount() + 1);
                    doubt.getDislikes().put(ctrl.getUid(), true);
                }
                mutableData.setValue(doubt);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if(checkLike){
                    Doubt doubt = dataSnapshot.getValue(Doubt.class);
                    if(doubt.getLikes().containsKey(ctrl.getUid())){
                        onLikeClicked(postRef, false);
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return doubts.size();
    }
}
