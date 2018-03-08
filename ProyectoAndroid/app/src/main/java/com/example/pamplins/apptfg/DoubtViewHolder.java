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

    public TextView titleView;
    public TextView authorView;
    //public ImageView starView;
    //public TextView numStarsView;
    public TextView bodyView;
    public TextView date;
    public ImageView img;
    public TextView numLikes;
    public ImageView like;
    public TextView numDisLikes;
    public ImageView dislike;
    public TextView numComments;

    public DoubtViewHolder(View itemView) {
        super(itemView);
        //TODO code a organizar

        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        //starView = itemView.findViewById(R.id.likes);
        //numStarsView = itemView.findViewById(R.id.post_num_likes);
        bodyView = itemView.findViewById(R.id.post_description);
        date = itemView.findViewById(R.id.tv_date);
        img = itemView.findViewById(R.id.post_author_photo);
        like = itemView.findViewById(R.id.like);
        numLikes = itemView.findViewById(R.id.num_likes);

        dislike = itemView.findViewById(R.id.dislike);
        numDisLikes = itemView.findViewById(R.id.num_dislikes);

        numComments = itemView.findViewById(R.id.num_comments);

    }

    public void bindToPost(final Doubt doubt, final Activity activity, final Controller ctrl, final String uid, final DatabaseReference postRef) {
        titleView.setText(doubt.getTitle());
        authorView.setText(doubt.getAuthor());

        //TODO code a organizar

        final DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child("post-comments").child(postRef.getKey());
        globalPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numComments.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (doubt.getDescription().trim().length() > 100) {
            bodyView.setText(doubt.getDescription().substring(0, 100) + "...");
        } else {
            bodyView.setText(doubt.getDescription());
        }
        date.setText(doubt.getDate());

        ctrl.drawImage(activity, img, uid);


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
