package com.example.pamplins.apptfg.HoldersAdapters;

/**
 * Created by Gustavo on 21/02/2018.
 */

import android.app.Activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;

public class DoubtViewHolder extends RecyclerView.ViewHolder {

    private TextView titleView;
    private TextView authorView;
    private TextView bodyView;
    private TextView date;
    private ImageView img;
    private TextView numLikes;
    private ImageView like;
    private TextView numDisLikes;
    private ImageView dislike;
    private TextView numAnswers;
    private TextView subject;

    public DoubtViewHolder(View itemView) {
        super(itemView);
        initElements(itemView);
    }

    private void initElements(View itemView){
        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        bodyView = itemView.findViewById(R.id.post_description);
        subject = itemView.findViewById(R.id.post_subject);
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

    public void bindToPost(final Doubt doubt, Activity activity, Controller ctrl) {
        titleView.setText(doubt.getTitle());
        authorView.setText(doubt.getUser().getUserName());
        subject.setText(doubt.getSubject());
        numAnswers.setText(String.valueOf(doubt.getnAnswers()));
        int count = doubt.getDescription().split("\r\n|\r|\n").length;
        String desc = doubt.getDescription();
        if(count > 4){
            desc = desc.replace("\n", " ").replace("\r", " ");
            if(desc.length() > 20){
                bodyView.setText(doubt.getDescription().substring(0, 20) + "...");
            }
        }else{
            if (desc.length() > 100) {
                bodyView.setText(doubt.getDescription().substring(0, 100) + "...");

            }
            else {
                bodyView.setText(doubt.getDescription());
            }
        }
        date.setText(doubt.getDate());
        ctrl.showProfileImage(activity, doubt, img);
    }

    public void bindLikes (Doubt doubt, View.OnClickListener clickListener){
        numLikes.setText(String.valueOf(doubt.getLikesCount()));
        like.setOnClickListener(clickListener);
    }

    public void bindDisLikes (Doubt doubt, View.OnClickListener clickListener){
        numDisLikes.setText(String.valueOf(doubt.getDislikesCount()));
        dislike.setOnClickListener(clickListener);
    }

}
