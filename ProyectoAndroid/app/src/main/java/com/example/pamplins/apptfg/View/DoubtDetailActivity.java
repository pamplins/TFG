package com.example.pamplins.apptfg.View;

/**
 * Created by Gustavo on 21/02/2018.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.AnswerAdapter;
import com.example.pamplins.apptfg.HoldersAdapters.ImageViewAdapter;
import com.example.pamplins.apptfg.Model.Answer;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoubtDetailActivity extends AppCompatActivity {
    private DatabaseReference doubtReference;
    private ValueEventListener doubtListener;
    private String doubtKey;

    private TextView tvAuthor;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvDescription;
    private TextView tvSubject;
    private EditText etAnswer;
    private ImageView img;
    private Button btnAnswer;

    public TextView numLikes;
    public ImageView like;
    public TextView numDisLikes;
    public ImageView dislike;
    public TextView numAnswers;

    private Controller ctrl;

    private Doubt currentDoubt;
    private RecyclerView mRecycler;
    private RecyclerView mRecycler_items;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Answer, AnswerAdapter.AnswerViewHolder> mAdapter;
    private DatabaseReference mDatabase;

    private Button btnAdvAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_doubt);

        doubtKey = getIntent().getStringExtra(Constants.KEY_DOUBT);
        if (doubtKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        initElements();
        showAnswersDoubt();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void initElements(){
        ctrl = Controller.getInstance();
        doubtReference = ctrl.getDoubtReference(doubtKey);
        mDatabase = ctrl.getDB().getReference();
        initDoubtItems();
        initAnswerItems();
    }

    private void initAnswerItems() {
        etAnswer = findViewById(R.id.field_answer_text);
        mRecycler = this.findViewById(R.id.recycler_answers);
        mRecycler.setVisibility(View.GONE);
        btnAnswer = findViewById(R.id.button_post_answer);
        btnAnswer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ctrl.writeAnswerDB(currentDoubt, doubtKey, etAnswer, doubtReference, btnAnswer);
                Utils.hideKeyboard(DoubtDetailActivity.this);

            }
        });
        btnAdvAnswer = findViewById(R.id.button_advance_answer);
        btnAdvAnswer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(DoubtDetailActivity.this, AdvAnswerActivity.class);
                startActivity(i);
            }
        });


    }

    private void initDoubtItems() {
        tvAuthor = findViewById(R.id.post_author);
        tvTitle = findViewById(R.id.post_title);
        tvDate = findViewById(R.id.tv_date);
        tvDescription = findViewById(R.id.post_description);
        tvSubject = findViewById(R.id.name_subject);
        img = findViewById(R.id.post_author_photo);
        like = findViewById(R.id.like);
        numLikes = findViewById(R.id.num_likes);
        dislike = findViewById(R.id.dislike);
        numDisLikes = findViewById(R.id.num_dislikes);
        numAnswers = findViewById(R.id.num_answers);

    }


    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDoubt = dataSnapshot.getValue(Doubt.class);
                Toolbar myToolbar = findViewById(R.id.my_toolbar);
                setSupportActionBar(myToolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(currentDoubt.getTitle());
                myToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorButton), PorterDuff.Mode.SRC_ATOP);
                showMainDoubt();
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


    private void showAnswersDoubt() {
        mManager = new LinearLayoutManager(this);
        mManager.setStackFromEnd(true);

        mManager.setReverseLayout(false);
        mRecycler.setLayoutManager(mManager);

        /*options = new FirebaseRecyclerOptions.Builder<Answer>()
                .setQuery(mDatabase.child(Constants.REF_POST_ANSWERS).child(doubtKey).orderByChild("likesCount"), Answer.class)
                .build();
        */

        final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Answer>()
                .setQuery(mDatabase.child(Constants.REF_POST_ANSWERS).child(doubtKey), Answer.class)
                .build();

        setAnswerAdapter(options);
    }

    private void setAnswerAdapter(FirebaseRecyclerOptions options) {
        mAdapter = new AnswerAdapter(options, this, doubtKey, mDatabase, ctrl);
        mRecycler.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecycler.scrollToPosition(mAdapter.getItemCount());
                if(mAdapter.getItemCount() > 0 ){
                    mRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    private void putDisLikes(){
        bindDisLikes(currentDoubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(doubtKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(currentDoubt.getUid()).child(doubtKey);
                ctrl.onDisLikeClicked(globalPostRef, true);
                ctrl.onDisLikeClicked(userPostRef, true);
            }
        });
    }

    private void putLikes(){
        bindLikes(currentDoubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(doubtKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(currentDoubt.getUid()).child(doubtKey);
                ctrl.onLikeClicked(globalPostRef, true);
                ctrl.onLikeClicked(userPostRef, true);
            }
        });
    }

    private void showMainDoubt(){
        tvAuthor.setText(currentDoubt.getAuthor());
        tvTitle.setText(currentDoubt.getTitle());
        tvDescription.setText(currentDoubt.getDescription());
        tvSubject.setText(currentDoubt.getSubject());
        tvDate.setText(currentDoubt.getDate());
        numAnswers.setText(String.valueOf(currentDoubt.getnAnswers()));
        ctrl.showProfileImage(this, currentDoubt.getUrlProfileImage(), img);
        if(currentDoubt.getUrlsImages() != null){
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
        ImageViewAdapter imageViewAdapter = new ImageViewAdapter(this, currentDoubt.getUrlsImages());
        mRecycler_items.setAdapter(imageViewAdapter);
    }

    private void checkLikesDis() {
        if (currentDoubt.getLikes().containsKey(ctrl.getUid())) {
            like.setImageResource(R.drawable.like_ac);
        } else {
            like.setImageResource(R.drawable.like);
        }
        if (currentDoubt.getDislikes().containsKey(ctrl.getUid())) {
            dislike.setImageResource(R.drawable.dislike_ac);
        } else {
            dislike.setImageResource(R.drawable.dislike);
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

    @Override
    public void onStop() {
        super.onStop();
        if (doubtListener != null) {
            doubtReference.removeEventListener(doubtListener);
        }

    }



}