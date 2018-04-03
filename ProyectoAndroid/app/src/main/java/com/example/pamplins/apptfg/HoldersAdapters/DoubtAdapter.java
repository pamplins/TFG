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

//TODO poner codigo en comun de AnswerAdapter y DoubtAdapter
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
                onLikeClicked(globalPostRef,true);
                onLikeClicked(userPostRef, true);
        }
        });

        viewHolder.bindDisLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                onDisLikeClicked(globalPostRef, true);
                onDisLikeClicked(userPostRef, true);
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

}
