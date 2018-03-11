package com.example.pamplins.apptfg;

/**
 * Created by Gustavo on 21/02/2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Fragments.HomeFragment;
import com.example.pamplins.apptfg.Model.Doubt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

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
    private TextView numComments;

    public DoubtViewHolder(View itemView) {
        super(itemView);
        initElements(itemView);
    }

    private void initElements(View itemView){
        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        bodyView = itemView.findViewById(R.id.post_description);
        date = itemView.findViewById(R.id.tv_date);
        img = itemView.findViewById(R.id.post_author_photo);
        like = itemView.findViewById(R.id.like);
        numLikes = itemView.findViewById(R.id.num_likes);
        dislike = itemView.findViewById(R.id.dislike);
        numDisLikes = itemView.findViewById(R.id.num_dislikes);
        numComments = itemView.findViewById(R.id.num_comments);
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
        numComments.setText(String.valueOf(doubt.getnComments()));
         if (doubt.getDescription().trim().length() > 100) {
            bodyView.setText(doubt.getDescription().substring(0, 100) + "...");
        } else {
            bodyView.setText(doubt.getDescription());
        }
        date.setText(doubt.getDate());
        ctrl.showImage(activity, doubt, img);
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
