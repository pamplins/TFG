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
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
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
    private Controller ctrl;

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
        ctrl = Controller.getInstance();

    }

    public static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context ctx;
        private DatabaseReference databaseReference;
        private ChildEventListener childEventListener;
        private List<String> commentIds = new ArrayList<>();
        private List<Comment> comments = new ArrayList<>();
        private Activity activity;


        public CommentAdapter(final Context context, Activity activity, DatabaseReference ref) {
            ctx = context;
            databaseReference = ref;
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
            holder.tvAuthor.setText(comment.getUser().getUserName());
            holder.tvDescription.setText(comment.text);
            holder.ctrl.showImage(activity, comment, holder.img);

            checkLikesDis(comment, holder, holder.ctrl);
            //TODO hacer likes y dislikes en comments
            // Poner los valores en la caja de duda de home
           /* holder.bindLikes(comment, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference globalPostRef = databaseReference.child("post-comments").child(comment.getUid());
                    onLikeClicked(globalPostRef, holder);
                }
            });

            holder.bindDisLikes(comment, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference globalPostRef = databaseReference.child("post-comments").child(comment.getUid());
                    onDisLikeClicked(globalPostRef,holder);
                }
            });*/
        }

        private void onLikeClicked(DatabaseReference postRef, final CommentViewHolder holder) {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Comment comment = mutableData.getValue(Comment.class);
                    if (comment == null) {
                        return Transaction.success(mutableData);
                    }
                    if (comment.getLikes().containsKey(holder.ctrl.getUid())) {
                        // Unstar the post and remove self from stars
                        comment.setLikesCount(comment.getLikesCount() - 1);
                        comment.getLikes().remove(holder.ctrl.getUid());
                    } else {
                        // Star the post and add self to stars
                        comment.setLikesCount(comment.getLikesCount() + 1);
                        comment.getLikes().put(holder.ctrl.getUid(), true);
                    }

                    // Set value and report transaction success
                    mutableData.setValue(comment);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                }
            });
        }

        private void onDisLikeClicked(DatabaseReference postRef, final CommentViewHolder holder) {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Comment comment = mutableData.getValue(Comment.class);
                    if (comment == null) {
                        return Transaction.success(mutableData);
                    }
                    if (comment.getDislikes().containsKey(holder.ctrl.getUid())) {
                        // Unstar the post and remove self from stars
                        comment.setDislikesCount(comment.getDislikesCount() - 1);
                        comment.getDislikes().remove(holder.ctrl.getUid());
                    } else {
                        // Star the post and add self to stars
                        comment.setDislikesCount(comment.getDislikesCount() + 1);
                        comment.getDislikes().put(holder.ctrl.getUid(), true);
                    }
                    // Set value and report transaction success
                    mutableData.setValue(comment);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                }
            });
        }

        private void checkLikesDis(Comment comment, CommentViewHolder holder, Controller ctrl) {
            // TODO si le da like y el dislike estaba ya activo. qitar dislike (restando de la mapHash) y activar like (+1 hashMap)
            if (comment.getLikes().containsKey(ctrl.getUid())) {
                holder.like.setImageResource(R.drawable.like_ac);
            } else {
                holder.like.setImageResource(R.drawable.like);
            }
            if (comment.getDislikes().containsKey(ctrl.getUid())) {
                holder.dislike.setImageResource(R.drawable.dislike_ac);
            } else {
                holder.dislike.setImageResource(R.drawable.dislike);
            }
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


    public void bindLikes (Comment comment, View.OnClickListener clickListener){
        numLikes.setText(String.valueOf(comment.getLikesCount()));
        like.setOnClickListener(clickListener);
    }

    public void bindDisLikes (Comment comment, View.OnClickListener clickListener){
        numDisLikes.setText(String.valueOf(comment.getDislikesCount()));
        dislike.setOnClickListener(clickListener);
    }




}


