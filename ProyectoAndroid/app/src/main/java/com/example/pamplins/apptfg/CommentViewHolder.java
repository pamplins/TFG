package com.example.pamplins.apptfg;

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
import com.example.pamplins.apptfg.Model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gustavo on 24/02/2018.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView tvAuthor;
    public TextView tvDescription;
    public ImageView img;
    public TextView numLikes;
    public ImageView like;
    public TextView numDisLikes;
    public ImageView dislike;


    public CommentViewHolder(View itemView) {
        super(itemView);

        tvAuthor = itemView.findViewById(R.id.comment_author);
        tvDescription = itemView.findViewById(R.id.comment_body);
        img = itemView.findViewById(R.id.comment_photo);
        like = itemView.findViewById(R.id.like_comment);
        numLikes = itemView.findViewById(R.id.num_likes_comment);

        /*dislike = itemView.findViewById(R.id.dislike_comment);
        numDisLikes = itemView.findViewById(R.id.num_dislikes_comment);*/
    }


    public static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context ctx;
        private DatabaseReference databaseReference;
        private ChildEventListener childEventListener;

        private List<String> commentIds = new ArrayList<>();
        private List<Comment> comments = new ArrayList<>();
        private Activity activity;
        public Controller ctrl;
        public CommentAdapter(final Context context, Activity activity, DatabaseReference ref) {
            ctx = context;
            databaseReference = ref;
            ctrl = Controller.getInstance();
            this.activity = activity;
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentIds.add(dataSnapshot.getKey());
                    comments.add(comment);
                    notifyItemInserted(comments.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = commentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        comments.set(commentIndex, newComment);

                        notifyItemChanged(commentIndex);
                    } else {
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = commentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        commentIds.remove(commentIndex);
                        comments.remove(commentIndex);

                        notifyItemRemoved(commentIndex);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                    // Ver que si un comentario tiene muchos votos. ponerlo como primera posicion en el foro ya que es la top response
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ctx, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);

            this.childEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }
        //
        @Override
        public void onBindViewHolder(final CommentViewHolder holder, int position) {
            final Comment comment = comments.get(position);
            holder.tvAuthor.setText(comment.author);
            holder.tvDescription.setText(comment.text);
            ctrl.drawImage(activity, holder.img, comment.uid);
            //TODO hacer comments likes dislikes
            /*holder.numLikes.setText(String.valueOf(comment.getLikesCount()));
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        public void cleanupListener() {
            if (childEventListener != null) {
                databaseReference.removeEventListener(childEventListener);
            }
        }

    }

}


