package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.arch.lifecycle.HolderFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Comment;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
import com.example.pamplins.apptfg.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gustavo on 24/02/2018.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private TextView tvAuthor;
    private TextView tvDescription;
    private ImageView img;
    private TextView numLikes;
    private ImageView like;
    private TextView numDisLikes;
    private ImageView dislike;
    private TextView date;

    public CommentViewHolder(View itemView) {
        super(itemView);
        initElements();
    }

    private void initElements(){
        tvAuthor = itemView.findViewById(R.id.comment_author);
        tvDescription = itemView.findViewById(R.id.comment_body);
        img = itemView.findViewById(R.id.comment_photo);
        like = itemView.findViewById(R.id.like_comment);
        numLikes = itemView.findViewById(R.id.num_likes_comment);
        dislike = itemView.findViewById(R.id.dislike_comment);
        numDisLikes = itemView.findViewById(R.id.num_dislikes_comment);
        date = itemView.findViewById(R.id.tv_date_comment);
    }

    public ImageView getLike() {
        return like;
    }

    public ImageView getDislike() {
        return dislike;
    }

    public void bindToPost(final Comment comment, Activity activity, Controller ctrl) {
        tvAuthor.setText(comment.getUser().getUserName());
        tvDescription.setText(comment.getText());
        date.setText(comment.getDate());
        ctrl.showImage(activity, comment, img);
    }

    public void bindLikes (Comment comment, View.OnClickListener clickListener){
        numLikes.setText(String.valueOf(comment.getLikesCount()));
        like.setOnClickListener(clickListener);
    }

    public void bindDisLikes (Comment comment, View.OnClickListener clickListener){
        numDisLikes.setText(String.valueOf(comment.getDislikesCount()));
        dislike.setOnClickListener(clickListener);
    }

}


