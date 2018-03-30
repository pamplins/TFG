package com.example.pamplins.apptfg.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Model.Comment;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Gustavo on 15/02/2018.
 */

public class Controller {

    private static Controller ctrl = null;
    private static DatabaseReference db;
    private Controller (){

    }

    public static Controller getInstance(){
        if(ctrl == null){
            ctrl = new Controller();
        }
        return ctrl;
    }

    public void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public Query getUsersbyUserName(){
        return FirebaseDatabase.getInstance().getReference(Constants.REF_USERS).orderByChild(Constants.REF_USERNAME);
    }

    public static void writeUserDB(String uid, String userName, String email, String spinnerItem, Bitmap bit, String imagePath, int action) {
        uploadImageProfile(uid, bit, Constants.REF_PROFILE_IMAGES, imagePath, userName, email, spinnerItem, action);

    }

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

    public static void uploadImageProfile(final String uid, Bitmap bit, String folder, String path, final String userName, final String email, final String spinnerItem, final int action) {
        String ref = folder + uid + "/" + path; // string de la ruta a la que ira
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference().child(ref);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        mStorageRef.putBytes(data);
        UploadTask uploadTask = mStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //TODO el usuario no se ha podido registrar porque no se ha podido subir la foto
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                if(action == 0){
                    User user = new User(userName, email, spinnerItem, downloadUrl.toString());
                    db = FirebaseDatabase.getInstance().getReference();
                    db.child("users").child(uid).setValue(user);
                }// Si no es 0, significa que sube foto de comentario o duda
                else{
                    //TODO hacer multi-path updates -- ITERACION 5
                    db = FirebaseDatabase.getInstance().getReference();
                    db.child("users").child(uid).child("urlProfileImage").setValue(downloadUrl.toString());
                    db.child("users").child(uid).child("urlProfileImage").setValue(downloadUrl.toString());

                }
            }
        });
    }


    public String getDate(){
        String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        return date+","+hour+":"+minute;
    }

    public void showProfileImage(Activity activity, Object obj, ImageView img) {
        String url;
        if(obj.getClass().equals(Doubt.class)){
            url = ((Doubt)obj).getUser().getUrlProfileImage();
        }else{
            url = ((Comment)obj).getUser().getUrlProfileImage();
        }
        Glide.with(activity)
                .load(url)
                .into(img);
    }
}