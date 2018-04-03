package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;

import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Answer;

import com.example.pamplins.apptfg.R;

/**
 * Created by Gustavo on 24/02/2018.
 */

public class AnswerViewHolder extends RecyclerView.ViewHolder {

    private TextView tvAuthor;
    private TextView tvDescription;
    private ImageView img;
    private TextView numLikes;
    private ImageView like;
    private TextView numDisLikes;
    private ImageView dislike;
    private TextView date;

    public AnswerViewHolder(View itemView) {
        super(itemView);
        initElements();
    }

    private void initElements(){
        tvAuthor = itemView.findViewById(R.id.answer_author);
        tvDescription = itemView.findViewById(R.id.answer_description);
        img = itemView.findViewById(R.id.answer_photo);
        like = itemView.findViewById(R.id.like_answer);
        numLikes = itemView.findViewById(R.id.num_likes_answer);
        dislike = itemView.findViewById(R.id.dislike_answer);
        numDisLikes = itemView.findViewById(R.id.num_dislikes_answer);
        date = itemView.findViewById(R.id.tv_date_answer);
    }

    public ImageView getLike() {
        return like;
    }

    public ImageView getDislike() {
        return dislike;
    }

    public void bindToPost(final Answer answer, Activity activity, Controller ctrl) {
        tvAuthor.setText(answer.getUser().getUserName());
        tvDescription.setText(answer.getText());
        date.setText(answer.getDate());
        ctrl.showProfileImage(activity, answer, img);
    }

    public void bindLikes (Answer answer, View.OnClickListener clickListener){
        numLikes.setText(String.valueOf(answer.getLikesCount()));
        like.setOnClickListener(clickListener);
    }

    public void bindDisLikes (Answer answer, View.OnClickListener clickListener){
        numDisLikes.setText(String.valueOf(answer.getDislikesCount()));
        dislike.setOnClickListener(clickListener);
    }

}


