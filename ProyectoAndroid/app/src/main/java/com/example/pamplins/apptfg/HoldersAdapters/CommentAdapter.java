package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Comment;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

/**
 * Created by Gustavo on 12/03/2018.
 */

public class CommentAdapter extends FirebaseRecyclerAdapter<Comment, CommentViewHolder> {

    private Activity activity;
    private String doubtKey;
    private DatabaseReference mDatabase;
    private Controller ctrl;

    public CommentAdapter(FirebaseRecyclerOptions<Comment> options, Activity activity, String doubtKey, DatabaseReference ref, Controller ctrl) {
        super(options);
        this.activity = activity;
        this.doubtKey = doubtKey;
        this.mDatabase = ref;
        this.ctrl = ctrl;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false);
        return new CommentViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(final CommentViewHolder viewHolder, int position, final Comment comment) {
        final DatabaseReference postRefComment = getRef(viewHolder.getAdapterPosition());
        final String postKeyComment = postRefComment.getKey();
        checkLikesDisComment(comment, viewHolder);
        viewHolder.bindToPost(comment, activity, ctrl);
        votesDoubt(comment, viewHolder, postKeyComment);
    }

    private void votesDoubt(Comment comment, CommentViewHolder viewHolder, final String postKeyComment) {
        viewHolder.bindLikes(comment, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_POST_COMMENTS).child(doubtKey).child(postKeyComment);
                onLikeClickedComment(globalPostRef, true);
            }
        });

        viewHolder.bindDisLikes(comment, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_POST_COMMENTS).child(doubtKey).child(postKeyComment);
                onDisLikeClickedComment(globalPostRef, true);
            }
        });
    }


    private void checkLikesDisComment(Comment comment, CommentViewHolder viewHolder){
        if (comment.getLikes().containsKey(ctrl.getUid())) {
            viewHolder.getLike().setImageResource(R.drawable.like_ac);
        } else {
            viewHolder.getLike().setImageResource(R.drawable.like);
        }
        if (comment.getDislikes().containsKey(ctrl.getUid())) {
            viewHolder.getDislike().setImageResource(R.drawable.dislike_ac);
        } else {
            viewHolder.getDislike().setImageResource(R.drawable.dislike);
        }

    }

    private void onLikeClickedComment(final DatabaseReference postRef, final boolean checkDisLike) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Comment comment = mutableData.getValue(Comment.class);
                if (comment == null) {
                    return Transaction.success(mutableData);
                }
                if (comment.getLikes().containsKey(ctrl.getUid())) {
                    comment.setLikesCount(comment.getLikesCount() - 1);
                    comment.getLikes().remove(ctrl.getUid());
                } else {
                    comment.setLikesCount(comment.getLikesCount() + 1);
                    comment.getLikes().put(ctrl.getUid(), true);
                }
                mutableData.setValue(comment);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if(checkDisLike){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    if(comment.getDislikes().containsKey(ctrl.getUid())){
                        onDisLikeClickedComment(postRef, false);
                    }
                }
            }
        });
    }

    private void onDisLikeClickedComment(final DatabaseReference postRef, final boolean checkLike) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Comment comment = mutableData.getValue(Comment.class);
                if (comment == null) {
                    return Transaction.success(mutableData);
                }
                if (comment.getDislikes().containsKey(ctrl.getUid())) {
                    comment.setDislikesCount(comment.getDislikesCount() - 1);
                    comment.getDislikes().remove(ctrl.getUid());
                } else {
                    comment.setDislikesCount(comment.getDislikesCount() + 1);
                    comment.getDislikes().put(ctrl.getUid(), true);
                }
                mutableData.setValue(comment);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if(checkLike){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    if(comment.getLikes().containsKey(ctrl.getUid())){
                        onLikeClickedComment(postRef, false);
                    }
                }
            }
        });
    }


}



