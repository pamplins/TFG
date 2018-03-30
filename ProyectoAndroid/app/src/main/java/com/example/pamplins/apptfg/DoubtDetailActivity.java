package com.example.pamplins.apptfg;

/**
 * Created by Gustavo on 21/02/2018.
 */

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.CommentAdapter;
import com.example.pamplins.apptfg.HoldersAdapters.CommentViewHolder;
import com.example.pamplins.apptfg.HoldersAdapters.ImageViewAdapter;
import com.example.pamplins.apptfg.Model.Comment;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DoubtDetailActivity extends AppCompatActivity {
    private DatabaseReference doubtReference;
    private DatabaseReference commentsReference;
    private ValueEventListener doubtListener;
    private String doubtKey;

    private TextView tvAuthor;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvDescription;
    private EditText etComment;
    private ImageView img;
    private Button btnComment;

    public TextView numLikes;
    public ImageView like;
    public TextView numDisLikes;
    public ImageView dislike;
    public TextView numComments;

    private Controller ctrl;

    private Doubt currentdDoubt;
    private RecyclerView mRecycler;
    private RecyclerView mRecycler_items;

    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> mAdapter;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_doubt);
        doubtKey = getIntent().getStringExtra(Constants.KEY_DOUBT);
        if (doubtKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        initElements();
        iniCommentSection();
    }


    private void initElements(){
        ctrl = Controller.getInstance();
        doubtReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.REF_DOUBTS).child(doubtKey);
        commentsReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.REF_POST_COMMENTS).child(doubtKey);

        tvAuthor = findViewById(R.id.post_author);
        tvTitle = findViewById(R.id.post_title);
        tvDate = findViewById(R.id.tv_date);
        tvDescription = findViewById(R.id.post_description);
        etComment = findViewById(R.id.field_comment_text);
        img = findViewById(R.id.post_author_photo);
        btnComment = findViewById(R.id.button_post_comment);

        like = findViewById(R.id.like);
        numLikes = findViewById(R.id.num_likes);

        dislike = findViewById(R.id.dislike);
        numDisLikes = findViewById(R.id.num_dislikes);
        numComments = findViewById(R.id.num_comments);

        //btnComment.setOnClickListener(this);
        btnComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                postComment();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = this.findViewById(R.id.recycler_comments);
        mRecycler.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener postListener = new ValueEventListener() {
            // Aqui se actualiza la informacion de la duda al abrirla
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentdDoubt = dataSnapshot.getValue(Doubt.class);
                setElementsDoubt();
                putLikes();
                putDisLikes();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        doubtReference.addValueEventListener(postListener);
        doubtListener = postListener;
    }


    private void iniCommentSection() {
        //mRecycler = this.findViewById(R.id.recycler_comments);
        mManager = new LinearLayoutManager(this);
        //mManager.setStackFromEnd(true);
        //mManager.setReverseLayout(true);
        mRecycler.setLayoutManager(mManager);

        /*options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(mDatabase.child(Constants.REF_POST_COMMENTS).child(doubtKey).orderByChild("likesCount"), Comment.class)
                .build();
        */

        final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(mDatabase.child(Constants.REF_POST_COMMENTS).child(doubtKey), Comment.class)
                .build();

        setCommentAdapter(options);
    }

    private void setCommentAdapter(FirebaseRecyclerOptions options) {
        mAdapter = new CommentAdapter(options, this, doubtKey, mDatabase, ctrl);
        mRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.startListening();
        mRecycler.setVisibility(View.VISIBLE);
    }

    private void putDisLikes(){
        bindDisLikes(currentdDoubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(doubtKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(currentdDoubt.getUid()).child(doubtKey);
                onDisLikeClicked(globalPostRef, true);
                onDisLikeClicked(userPostRef, true);
            }
        });
    }

    private void putLikes(){
        bindLikes(currentdDoubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(doubtKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(currentdDoubt.getUid()).child(doubtKey);
                onLikeClicked(globalPostRef, true);
                onLikeClicked(userPostRef, true);
            }
        });
    }

    private void setElementsDoubt(){
        tvAuthor.setText(currentdDoubt.getUser().getUserName());
        tvTitle.setText(currentdDoubt.getTitle());
        tvDescription.setText(currentdDoubt.getDescription());
        tvDate.setText(currentdDoubt.getDate());
        numComments.setText(String.valueOf(currentdDoubt.getnComments()));
        ctrl.showProfileImage(this, currentdDoubt, img);
        if(currentdDoubt.getUrlsImages() != null){
            initCarousel();
        }else{
            TextView tv_attachedFiles = findViewById(R.id.tv_attachedFiles);
            tv_attachedFiles.setVisibility(View.GONE);
        }
        checkLikesDis();

    }

    private void initCarousel(){
        mRecycler_items = findViewById(R.id.recycle_items_doubt);
        mRecycler_items.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecycler_items.setLayoutManager(linearLayoutManager);
        ImageViewAdapter imageViewAdapter = new ImageViewAdapter(this, currentdDoubt.getUrlsImages());
        mRecycler_items.setAdapter(imageViewAdapter);
    }

    private void checkLikesDis() {
        if (currentdDoubt.getLikes().containsKey(ctrl.getUid())) {
            like.setImageResource(R.drawable.like_ac);
        } else {
            like.setImageResource(R.drawable.like);
        }
        if (currentdDoubt.getDislikes().containsKey(ctrl.getUid())) {
            dislike.setImageResource(R.drawable.dislike_ac);
        } else {
            dislike.setImageResource(R.drawable.dislike);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (doubtListener != null) {
            doubtReference.removeEventListener(doubtListener);
        }
    }

    private void setBtnDoubt(boolean enabled) {
        btnComment.setEnabled(enabled);
    }
    public void bindLikes (Doubt doubt, View.OnClickListener clickListener){
        numLikes.setText(String.valueOf(doubt.getLikesCount()));
        like.setOnClickListener(clickListener);
    }

    public void bindDisLikes (Doubt doubt, View.OnClickListener clickListener){
        numDisLikes.setText(String.valueOf(doubt.getDislikesCount()));
        dislike.setOnClickListener(clickListener);
    }

    private void onLikeClicked(final DatabaseReference postRef, final boolean checkDisLike) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Doubt doubt = mutableData.getValue(Doubt.class);
                if (doubt == null) {
                    return Transaction.success(mutableData);
                }
                if (doubt.getLikes().containsKey(ctrl.getUid())) {
                    doubt.setLikesCount(doubt.getLikesCount() - 1);
                    doubt.getLikes().remove(ctrl.getUid());
                } else {
                    doubt.setLikesCount(doubt.getLikesCount() + 1);
                    doubt.getLikes().put(ctrl.getUid(), true);
                }
                mutableData.setValue(doubt);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if(checkDisLike){
                    Doubt doubt = dataSnapshot.getValue(Doubt.class);
                    if(doubt.getDislikes().containsKey(ctrl.getUid())){
                        onDisLikeClicked(postRef, false);
                    }
                }
            }
        });
    }

    private void onDisLikeClicked(final DatabaseReference postRef, final boolean checkLike) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Doubt doubt = mutableData.getValue(Doubt.class);
                if (doubt == null) {
                    return Transaction.success(mutableData);
                }
                if (doubt.getDislikes().containsKey(ctrl.getUid())) {
                    doubt.setDislikesCount(doubt.getDislikesCount() - 1);
                    doubt.getDislikes().remove(ctrl.getUid());
                } else {
                    doubt.setDislikesCount(doubt.getDislikesCount() + 1);
                    doubt.getDislikes().put(ctrl.getUid(), true);
                }
                mutableData.setValue(doubt);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                if(checkLike){
                    Doubt doubt = dataSnapshot.getValue(Doubt.class);
                    if(doubt.getLikes().containsKey(ctrl.getUid())){
                        onLikeClicked(postRef, false);
                    }
                }
            }
        });
    }

    private void postComment() {
        final String uid = ctrl.getUid();
        final String commentText = etComment.getText().toString();
        if(commentText.isEmpty()){
            etComment.setError("Entra comentario");
        }else {
            setBtnDoubt(false); // evitar multiples creaciones de dudas
            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        final String authorName = user.getUserName();

                        ctrl.getUsersbyUserName().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    User user = childSnapshot.getValue(User.class);
                                    if (user.getUserName().equals(authorName)) {
                                        Comment comment = new Comment(uid, commentText, ctrl.getDate(), user);
                                        Map<String, Object> commentValues = comment.toMap();
                                        commentsReference.push().setValue(commentValues);
                                        etComment.setText(null);
                                        currentdDoubt.setnComments(currentdDoubt.getnComments() + 1);
                                        doubtReference.child("nComments").setValue(currentdDoubt.getnComments());
                                        ctrl.hideKeyboard(DoubtDetailActivity.this);
                                        setBtnDoubt(true);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                setBtnDoubt(true);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        setBtnDoubt(true);
                    }
                });
        }
    }
}
