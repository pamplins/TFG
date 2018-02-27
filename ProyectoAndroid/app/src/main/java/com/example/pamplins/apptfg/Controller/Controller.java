package com.example.pamplins.apptfg.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Model.User;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Gustavo on 15/02/2018.
 */

public class Controller {

    private static Controller ctrl = null;
    private static User user;
    private static FirebaseUser mAuth;
    private static DatabaseReference db;

    private Controller (){

    }

    public static Controller getInstance(){
        return ctrl == null ? new Controller() : ctrl;
    }

    public void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void writeUserDB(String uid, String userName, String email, String spinnerItem) {
        user = new User(userName, email, spinnerItem);
        db = FirebaseDatabase.getInstance().getReference();
        db.child("users").child(uid).setValue(user);
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static FirebaseUser getCurrentUser(){
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        if(mAuth != null){
            return mAuth;
        }
        return null;
    }
    /*
    public static User getUser(){
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    user = ds.getValue(User.class);
                    if(user != null){
                        if (user.getUid().equals(mAuth.getUid())) {
                            break;
                        }
                    }else{

                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        db = FirebaseDatabase.getInstance().getReference();
        db.child("users").addListenerForSingleValueEvent(eventListener);
        return user;
    }*/



    public boolean verifyPermissions(Activity activity) {
        int p_camera = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA);
        int p_storage = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if((p_camera == PackageManager.PERMISSION_GRANTED) && (p_storage == PackageManager.PERMISSION_GRANTED)){
            return true;
        }else{
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
            return false;
        }
    }

    /*
    public void drawImage(final Activity activity, final int component){

        // Reference to an image file in Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user_images/"+getUid()+"/image_profile.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageView img = activity.findViewById(component);
                img.buildDrawingCache();
                Bitmap bit = img.getDrawingCache();

                img.setImageBitmap(Utils.getCircularBitmap(bit));
                // Load the image using Glide
                Glide.with(activity.getBaseContext())
                        .load(uri)
                        .into(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    } */

}
