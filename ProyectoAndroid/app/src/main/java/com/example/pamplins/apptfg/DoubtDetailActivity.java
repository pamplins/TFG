package com.example.pamplins.apptfg;

/**
 * Created by Gustavo on 21/02/2018.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Comment;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DoubtDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DoubtDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference doubtReference;
    private DatabaseReference commentsReference;
    private ValueEventListener doubtListener;
    private String doubtKey;
    private CommentViewHolder.CommentAdapter commentAdapter;

    private TextView tvAuthor;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvDescription;
    private EditText etComment;
    private ImageView img;
    private Button btnComment;
    private RecyclerView commentsRecycler;

    public TextView numLikes;
    public ImageView like;
    public TextView numDisLikes;
    public ImageView dislike;
    public TextView numComments;

    private Controller ctrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_doubt);

        doubtKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (doubtKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        ctrl = Controller.getInstance();


        doubtReference = FirebaseDatabase.getInstance().getReference()
                .child("doubts").child(doubtKey);
        commentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(doubtKey);
        //TODO code a organizar

        tvAuthor = findViewById(R.id.post_author);
        tvTitle = findViewById(R.id.post_title);
        tvDate = findViewById(R.id.tv_date); //modificado
        tvDescription = findViewById(R.id.post_description);
        etComment = findViewById(R.id.field_comment_text);
        img = findViewById(R.id.post_author_photo);
        btnComment = findViewById(R.id.button_post_comment);
        commentsRecycler = findViewById(R.id.recycler_comments);

        like = findViewById(R.id.like);
        numLikes = findViewById(R.id.num_likes);

        dislike = findViewById(R.id.dislike);
        numDisLikes = findViewById(R.id.num_dislikes);
        numComments = findViewById(R.id.num_comments);

        btnComment.setOnClickListener(this);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener postListener = new ValueEventListener() {
            // Aqui se actualiza la informacion de la duda al abrirla
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Doubt doubt = dataSnapshot.getValue(Doubt.class);
                tvAuthor.setText(doubt.getAuthor());
                tvTitle.setText(doubt.getTitle());
                tvDescription.setText(doubt.getDescription());
                tvDate.setText(doubt.getDate());
                ctrl.drawImage(DoubtDetailActivity.this, img, doubt.getUid());

                if (doubt.getLikes().containsKey(ctrl.getUid())) {
                    like.setImageResource(R.drawable.like_ac);
                } else {
                    like.setImageResource(R.drawable.like);
                }
                if (doubt.getDislikes().containsKey(ctrl.getUid())) {
                    dislike.setImageResource(R.drawable.dislike_ac);
                } else {
                    dislike.setImageResource(R.drawable.dislike);
                }

                //TODO code a organizar
                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                bindLikes(doubt, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference globalPostRef = mDatabase.child("doubts").child(doubtKey);
                        DatabaseReference userPostRef = mDatabase.child("user_doubts").child(doubt.getUid()).child(doubtKey);
                        // Run two transactions
                        onLikeClicked(globalPostRef);
                        onLikeClicked(userPostRef);
                    }
                });

                bindDisLikes(doubt, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference globalPostRef = mDatabase.child("doubts").child(doubtKey);
                        DatabaseReference userPostRef = mDatabase.child("user_doubts").child(doubt.getUid()).child(doubtKey);
                        // Run two transactions
                        onDisLikeClicked(globalPostRef);
                        onDisLikeClicked(userPostRef);
                    }
                });


                final DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child("post-comments").child(doubtKey);
                globalPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        numComments.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(DoubtDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        doubtReference.addValueEventListener(postListener);

        doubtListener = postListener;

        commentAdapter = new CommentViewHolder.CommentAdapter(this, DoubtDetailActivity.this, commentsReference);
        commentsRecycler.setAdapter(commentAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (doubtListener != null) {
            doubtReference.removeEventListener(doubtListener);
        }

        commentAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            postComment();
        }
    }

    public void bindLikes (Doubt doubt, View.OnClickListener clickListener){
        numLikes.setText(String.valueOf(doubt.getLikesCount()));
        like.setOnClickListener(clickListener);

    }

    public void bindDisLikes (Doubt doubt, View.OnClickListener clickListener){
        numDisLikes.setText(String.valueOf(doubt.getDislikesCount()));

        dislike.setOnClickListener(clickListener);

    }

    // [START post_stars_transaction]
    private void onLikeClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Doubt doubt = mutableData.getValue(Doubt.class);
                if (doubt == null) {
                    return Transaction.success(mutableData);
                }
                if (doubt.getLikes().containsKey(ctrl.getUid())) {
                    // Unstar the post and remove self from stars
                    doubt.setLikesCount(doubt.getLikesCount() - 1);
                    doubt.getLikes().remove(ctrl.getUid());
                } else {
                    // Star the post and add self to stars
                    doubt.setLikesCount(doubt.getLikesCount() + 1);
                    doubt.getLikes().put(ctrl.getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(doubt);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

    // [START post_stars_transaction]
    private void onDisLikeClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Doubt doubt = mutableData.getValue(Doubt.class);
                if (doubt == null) {
                    return Transaction.success(mutableData);
                }
                if (doubt.getDislikes().containsKey(ctrl.getUid())) {
                    // Unstar the post and remove self from stars
                    doubt.setDislikesCount(doubt.getDislikesCount() - 1);
                    doubt.getDislikes().remove(ctrl.getUid());
                } else {
                    // Star the post and add self to stars
                    doubt.setDislikesCount(doubt.getDislikesCount() + 1);
                    doubt.getDislikes().put(ctrl.getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(doubt);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

    private void postComment() {
        final String uid = ctrl.getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        final String authorName = user.getUserName();

                        final String commentText = etComment.getText().toString();
                        if(commentText.isEmpty()){
                            etComment.setError("Entra comentario");
                        }
                        if(!commentText.isEmpty()){
                            ctrl.getUsersbyUserName().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                        User user = childSnapshot.getValue(User.class);
                                        if(user.getUserName().equals(authorName)){
                                            String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                                            Comment comment = new Comment(uid, authorName, commentText, date);
                                            Map<String, Object> commentValues = comment.toMap();

                                            commentsReference.push().setValue(commentValues);
                                            etComment.setText(null);
                                            ctrl.hideKeyboard(DoubtDetailActivity.this);

                                            //TODO code a organizar
                                            final DatabaseReference globalPostRef = FirebaseDatabase.getInstance().getReference().child("post-comments").child(doubtKey);
                                            globalPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    numComments.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                            break;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



}
