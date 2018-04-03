package com.example.pamplins.apptfg;

/**
 * Created by Gustavo on 21/02/2018.
 */

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

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.AnswerAdapter;
import com.example.pamplins.apptfg.HoldersAdapters.AnswerViewHolder;
import com.example.pamplins.apptfg.HoldersAdapters.ImageViewAdapter;
import com.example.pamplins.apptfg.Model.Answer;
import com.example.pamplins.apptfg.Model.Doubt;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class DoubtDetailActivity extends AppCompatActivity {
    private DatabaseReference doubtReference;
    private DatabaseReference answersReference;
    private ValueEventListener doubtListener;
    private String doubtKey;

    private TextView tvAuthor;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvDescription;
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
    LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Answer, AnswerViewHolder> mAdapter;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_doubt);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ASIGNATURA X");

        myToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorButton), PorterDuff.Mode.SRC_ATOP);
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
        //TODO enviar estas referencias a la class ctontroller
        doubtReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.REF_DOUBTS).child(doubtKey);
        answersReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.REF_POST_ANSWERS).child(doubtKey);

        tvAuthor = findViewById(R.id.post_author);
        tvTitle = findViewById(R.id.post_title);
        tvDate = findViewById(R.id.tv_date);
        tvDescription = findViewById(R.id.post_description);
        etAnswer = findViewById(R.id.field_answer_text);
        img = findViewById(R.id.post_author_photo);
        btnAnswer = findViewById(R.id.button_post_answer);

        like = findViewById(R.id.like);
        numLikes = findViewById(R.id.num_likes);

        dislike = findViewById(R.id.dislike);
        numDisLikes = findViewById(R.id.num_dislikes);
        numAnswers = findViewById(R.id.num_answers);

        //btnAnswer.setOnClickListener(this);
        btnAnswer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ctrl.writeAnswerDB(currentDoubt, answersReference, etAnswer, doubtReference, btnAnswer, DoubtDetailActivity.this);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = this.findViewById(R.id.recycler_answers);
        mRecycler.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();

        ValueEventListener postListener = new ValueEventListener() {
            // Aqui se actualiza la informacion de la duda al abrirla
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDoubt = dataSnapshot.getValue(Doubt.class);

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
                onDisLikeClicked(globalPostRef, true);
                onDisLikeClicked(userPostRef, true);
            }
        });
    }

    private void putLikes(){
        bindLikes(currentDoubt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference globalPostRef = mDatabase.child(Constants.REF_DOUBTS).child(doubtKey);
                DatabaseReference userPostRef = mDatabase.child(Constants.REF_USER_DOUBTS).child(currentDoubt.getUid()).child(doubtKey);
                onLikeClicked(globalPostRef, true);
                onLikeClicked(userPostRef, true);
            }
        });
    }

    private void showMainDoubt(){
        tvAuthor.setText(currentDoubt.getUser().getUserName());
        tvTitle.setText(currentDoubt.getTitle());
        tvDescription.setText(currentDoubt.getDescription());
        tvDate.setText(currentDoubt.getDate());
        numAnswers.setText(String.valueOf(currentDoubt.getnAnswers()));
        ctrl.showProfileImage(this, currentDoubt, img);
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

    @Override
    public void onStop() {
        super.onStop();
        if (doubtListener != null) {
            doubtReference.removeEventListener(doubtListener);
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
}
