package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.View.DoubtDetailActivity;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Gustavo on 12/03/2018.
 */

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

        checkLikesDis(doubt, viewHolder.getLike(), viewHolder.getDislike());
        viewHolder.fillDoubt(doubt, activity, ctrl);
        votesDoubt(doubt, viewHolder, postKey);
    }

    public void checkLikesDis(Doubt doubt, ImageView like, ImageView dislike) {
        if (doubt.getLikes().containsKey(ctrl.getUid())) {
            like.setImageResource(R.drawable.like_ac);
        } else {
            like.setImageResource(R.drawable.like);
        }
        if (doubt.getDislikes().containsKey(ctrl.getUid())) {
            dislike.setImageResource(R.drawable.dislike_ac);
        } else {
            dislike.setImageResource(R.drawable.dislike);
        }
    }


    private  void votesDoubt(final Doubt doubt, DoubtViewHolder viewHolder, final String postKey){
        viewHolder.fillLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                ctrl.onLikeClicked(globalPostRef,true);
                ctrl.onLikeClicked(userPostRef, true);
        }
        });

        viewHolder.fillDisLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                ctrl.onDisLikeClicked(globalPostRef, true);
                ctrl.onDisLikeClicked(userPostRef, true);
            }
        });
    }

}
