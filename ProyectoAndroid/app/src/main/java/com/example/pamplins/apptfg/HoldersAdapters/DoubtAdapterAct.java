package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.View.DoubtDetailActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class DoubtAdapterAct extends RecyclerView.Adapter<DoubtAdapter.DoubtViewHolder> {
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
    public DoubtAdapter.DoubtViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_doubt, viewGroup, false);
        return new DoubtAdapter.DoubtViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DoubtAdapter.DoubtViewHolder holder, final int position) {
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
        holder.fillDoubt(doubt, activity, ctrl);
        votesDoubt(doubt, holder, keys.get(position));
    }


    private  void votesDoubt(final Doubt doubt, final DoubtAdapter.DoubtViewHolder viewHolder, final String postKey) {
        viewHolder.fillLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = FirebaseDatabase.getInstance().getReference().child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                ctrl.onLikeClicked(globalPostRef, true);
                ctrl.onLikeClicked(userPostRef, true);
                notifyDataSetChanged();
            }
        });

        viewHolder.fillDisLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = FirebaseDatabase.getInstance().getReference().child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                ctrl.onDisLikeClicked(globalPostRef, true);
                ctrl.onDisLikeClicked(userPostRef, true);
                notifyDataSetChanged();
            }
        });
    }


    private void checkLikesDis(Doubt doubt, DoubtAdapter.DoubtViewHolder viewHolder) {
        if (doubt.getLikes().containsKey(ctrl.getUid())) {
            viewHolder.like.setImageResource(R.drawable.like_ac);
        } else {
            viewHolder.getLike().setImageResource(R.drawable.like);
        }
        if (doubt.getDislikes().containsKey(ctrl.getUid())) {
            viewHolder.getDislike().setImageResource(R.drawable.dislike_ac);
        } else {
            viewHolder.getDislike().setImageResource(R.drawable.dislike);
        }
    }

    @Override
    public int getItemCount() {
        return doubts.size();
    }
}
