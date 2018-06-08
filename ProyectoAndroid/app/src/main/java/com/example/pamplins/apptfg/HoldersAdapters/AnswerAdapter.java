package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Answer;
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

public class AnswerAdapter extends FirebaseRecyclerAdapter<Answer, AnswerAdapter.AnswerViewHolder> {

    private Activity activity;
    private String doubtKey;
    private DatabaseReference mDatabase;
    private Controller ctrl;

    public AnswerAdapter(FirebaseRecyclerOptions<Answer> options, Activity activity, String doubtKey, DatabaseReference ref, Controller ctrl) {
        super(options);
        this.activity = activity;
        this.doubtKey = doubtKey;
        this.mDatabase = ref;
        this.ctrl = ctrl;
    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_answer, viewGroup, false);
        return new AnswerViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(final AnswerViewHolder viewHolder, int position, final Answer answer) {
        final DatabaseReference postRefAnswer = getRef(viewHolder.getAdapterPosition());
        final String postKeyAnswer = postRefAnswer.getKey();
        checkLikesDisAnswer(answer, viewHolder);
        viewHolder.bindToPost(answer, activity, ctrl);
        votesDoubt(answer, viewHolder, postKeyAnswer);
    }

    private void votesDoubt(Answer answer, AnswerViewHolder viewHolder, final String postKeyAnswer) {
        viewHolder.bindLikes(answer, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_POST_ANSWERS).child(doubtKey).child(postKeyAnswer);
                onLikeClickedAnswer(globalPostRef, true);
            }
        });

        viewHolder.bindDisLikes(answer, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_POST_ANSWERS).child(doubtKey).child(postKeyAnswer);
                onDisLikeClickedAnswer(globalPostRef, true);
            }
        });
    }


    private void checkLikesDisAnswer(Answer answer, AnswerViewHolder viewHolder){
        if (answer.getLikes().containsKey(ctrl.getUid())) {
            viewHolder.like.setImageResource(R.drawable.like_ac);
        } else {
            viewHolder.like.setImageResource(R.drawable.like);
        }
        if (answer.getDislikes().containsKey(ctrl.getUid())) {
            viewHolder.dislike.setImageResource(R.drawable.dislike_ac);
        } else {
            viewHolder.dislike.setImageResource(R.drawable.dislike);
        }

    }

    private void onLikeClickedAnswer(final DatabaseReference postRef, final boolean checkDisLike) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Answer answer = mutableData.getValue(Answer.class);
                if (answer == null) {
                    return Transaction.success(mutableData);
                }
                if (answer.getLikes().containsKey(ctrl.getUid())) {
                    answer.setLikesCount(answer.getLikesCount() - 1);
                    answer.getLikes().remove(ctrl.getUid());
                } else {
                    answer.setLikesCount(answer.getLikesCount() + 1);
                    answer.getLikes().put(ctrl.getUid(), true);
                }
                mutableData.setValue(answer);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if(checkDisLike){
                    Answer answer = dataSnapshot.getValue(Answer.class);
                    if(answer.getDislikes().containsKey(ctrl.getUid())){
                        onDisLikeClickedAnswer(postRef, false);
                    }
                }
            }
        });
    }

    private void onDisLikeClickedAnswer(final DatabaseReference postRef, final boolean checkLike) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Answer answer = mutableData.getValue(Answer.class);
                if (answer == null) {
                    return Transaction.success(mutableData);
                }
                if (answer.getDislikes().containsKey(ctrl.getUid())) {
                    answer.setDislikesCount(answer.getDislikesCount() - 1);
                    answer.getDislikes().remove(ctrl.getUid());
                } else {
                    answer.setDislikesCount(answer.getDislikesCount() + 1);
                    answer.getDislikes().put(ctrl.getUid(), true);
                }
                mutableData.setValue(answer);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if(checkLike){
                    Answer answer = dataSnapshot.getValue(Answer.class);
                    if(answer.getLikes().containsKey(ctrl.getUid())){
                        onLikeClickedAnswer(postRef, false);
                    }
                }
            }
        });
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {

        TextView tvAuthor;
        TextView tvDescription;
        ImageView img;
        TextView numLikes;
        ImageView like;
        TextView numDisLikes;
        ImageView dislike;
        TextView date;

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


        public void bindToPost(final Answer answer, Activity activity, Controller ctrl) {
            tvAuthor.setText(answer.getAuthor());
            tvDescription.setText(answer.getText());
            date.setText(answer.getDate());
            ctrl.showProfileImage(activity, answer.getUrlProfileImage(), img);
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

}



