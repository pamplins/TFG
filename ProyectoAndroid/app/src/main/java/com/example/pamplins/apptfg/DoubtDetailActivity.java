package com.example.pamplins.apptfg;

/**
 * Created by Gustavo on 21/02/2018.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Comment;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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



        tvAuthor = findViewById(R.id.post_author);
        tvTitle = findViewById(R.id.post_title);
        tvDate = findViewById(R.id.tv_date_comment);
        tvDescription = findViewById(R.id.post_description);
        etComment = findViewById(R.id.field_comment_text);
        img = findViewById(R.id.post_author_photo);
        btnComment = findViewById(R.id.button_post_comment);
        commentsRecycler = findViewById(R.id.recycler_comments);

        btnComment.setOnClickListener(this);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener postListener = new ValueEventListener() {
            // Aqui se actualiza la informacion del comentario
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Doubt doubt = dataSnapshot.getValue(Doubt.class);
                tvAuthor.setText(doubt.getAuthor());
                tvTitle.setText(doubt.getTitle());
                tvDescription.setText(doubt.getDescription());
                tvDate.setText(doubt.getDate());
                Bitmap bit = loadBitmapFromView(img);

                img.setImageBitmap(Utils.getCircularBitmap(bit));
                // Load the image using Glide
                Glide.with(DoubtDetailActivity.this)
                        .load(doubt.getUrlImagePerfil())
                        .into(img);

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


    private static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

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
                            // Reference to an image file in Firebase Storage
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user_images/"+ctrl.getUid()+"/image_profile.jpg");
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Comment comment = new Comment(uid, authorName, commentText, uri.toString());
                                    commentsReference.push().setValue(comment);
                                    etComment.setText(null);
                                    ctrl.hideKeyboard(DoubtDetailActivity.this);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

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
