package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.View.DoubtDetailActivity;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by Gustavo on 12/03/2018.
 */

public class DoubtAdapter  extends FirebaseRecyclerAdapter<Doubt, DoubtAdapter.DoubtViewHolder> {
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
    protected void onBindViewHolder(final DoubtViewHolder holder, int position, final Doubt doubt) {
        final DatabaseReference postRef = getRef(holder.getAdapterPosition());
        final String postKey = postRef.getKey();

        // Abrir una duda X de la lista de home
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DoubtDetailActivity.class);
                intent.putExtra(Constants.KEY_DOUBT, postKey);
                activity.startActivity(intent);
            }
        });

        checkLikesDis(doubt, holder);
        holder.fillDoubt(doubt, activity, ctrl);
        votesDoubt(doubt, holder, postKey);
    }

    public void checkLikesDis(Doubt doubt, DoubtViewHolder holder) {
        if (doubt.getLikes().containsKey(ctrl.getUid())) {
            holder.like.setImageResource(R.drawable.like_ac);
        } else {
            holder.like.setImageResource(R.drawable.like);
        }
        if (doubt.getDislikes().containsKey(ctrl.getUid())) {
            holder.dislike.setImageResource(R.drawable.dislike_ac);
        } else {
            holder.dislike.setImageResource(R.drawable.dislike);
        }
    }


    private  void votesDoubt(final Doubt doubt, DoubtViewHolder holder, final String postKey){
        holder.fillLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                ctrl.onLikeClicked(globalPostRef,true);
                ctrl.onLikeClicked(userPostRef, true);
        }
        });

        holder.fillDisLikes(doubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(postKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(doubt.getUid()).child(postKey);
                ctrl.onDisLikeClicked(globalPostRef, true);
                ctrl.onDisLikeClicked(userPostRef, true);
            }
        });
    }

    public static class DoubtViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        TextView authorView;
        TextView bodyView;
        TextView date;
        ImageView img;
        TextView numLikes;
        ImageView like;
        TextView numDisLikes;
        ImageView dislike;
        TextView numAnswers;
        TextView subject;

        public DoubtViewHolder(View itemView) {
            super(itemView);
            initElements(itemView);
        }

        private void initElements(View itemView) {
            titleView = itemView.findViewById(R.id.post_title);
            authorView = itemView.findViewById(R.id.post_author);
            bodyView = itemView.findViewById(R.id.post_description);
            subject = itemView.findViewById(R.id.name_subject);
            date = itemView.findViewById(R.id.tv_date);
            img = itemView.findViewById(R.id.post_author_photo);
            like = itemView.findViewById(R.id.like);
            numLikes = itemView.findViewById(R.id.num_likes);
            dislike = itemView.findViewById(R.id.dislike);
            numDisLikes = itemView.findViewById(R.id.num_dislikes);
            numAnswers = itemView.findViewById(R.id.num_answers);
        }

        public ImageView getLike() {
            return like;
        }

        public ImageView getDislike() {
            return dislike;
        }

        /**
         * Funcion encargada de añadir a los elementos los datos de la duda
         *
         * @param doubt
         * @param activity
         * @param ctrl
         */
        public void fillDoubt(final Doubt doubt, Activity activity, Controller ctrl) {
            titleView.setText(doubt.getTitle());
            authorView.setText(doubt.getUser().getUserName());
            subject.setText(doubt.getSubject());
            numAnswers.setText(String.valueOf(doubt.getnAnswers()));
            int count = doubt.getDescription().split("\r\n|\r|\n").length;
            String desc = doubt.getDescription();
            if (count > 4) {
                desc = desc.replace("\n", " ").replace("\r", " ");
                if (desc.length() > 20) {
                    bodyView.setText(doubt.getDescription().substring(0, 20) + "...");
                }
            } else {
                if (desc.length() > 100) {
                    bodyView.setText(doubt.getDescription().substring(0, 100) + "...");

                } else {
                    bodyView.setText(doubt.getDescription());
                }
            }
            date.setText(doubt.getDate());
            ctrl.showProfileImage(activity, doubt, img);
        }

        public void fillLikes(Doubt doubt, View.OnClickListener clickListener) {
            numLikes.setText(String.valueOf(doubt.getLikesCount()));
            like.setOnClickListener(clickListener);
        }

        public void fillDisLikes(Doubt doubt, View.OnClickListener clickListener) {
            numDisLikes.setText(String.valueOf(doubt.getDislikesCount()));
            dislike.setOnClickListener(clickListener);
        }

    }

    }
