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

/**
 * Created by Gustavo on 15/02/2018.
 */

public class Controller {

    private static Controller ctrl = null;
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

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public Query getUsersbyUserName(){
        return FirebaseDatabase.getInstance().getReference(Constants.REF_USERS).orderByChild(Constants.REF_USERNAME);
    }

    public static void writeUserDB(String uid, String userName, String email, String spinnerItem, Bitmap bit, String imagePath) {
        uploadImageProfile(uid, bit, Constants.REF_PROFILE_IMAGES, imagePath, userName, email, spinnerItem, 0);

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
        getCircularBitmap(bit).compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

                }
            }
        });
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void showImage(Activity activity, Object obj, ImageView img, int option) {
        switch (option){
            case 0:
                urlProfile(activity, obj, img);
                break;
            case 1:
                doubtImage(activity, obj, img);
                break;
        }

    }

    private void doubtImage(Activity activity, Object obj, ImageView img) {
        String url = ((Doubt)obj).getUrlImage();
        Glide.with(activity)
                .load(url)
                .into(img);
    }

    private void urlProfile(Activity activity, Object obj, ImageView img) {
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



/*public static User getUser(){
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
}



String ref = "user_images/"+uid+"/image_profile.jpg";


StorageReference storageRef = FirebaseStorage.getInstance().getReference();
storageRef.child(ref).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
    @Override
    public void onSuccess(Uri uri) {
        Bitmap bit = loadBitmapFromView(img);
        img.setImageBitmap(Utils.getCircularBitmap(bit));
        // Load the image using Glide
        Glide.with(activity)
                .load(uri.toString())
                .into(img);

    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception exception) {
        // Handle any errors
    }
});


private static Bitmap loadBitmapFromView(View v) {
Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
Canvas c = new Canvas(b);
v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
v.draw(c);
return b;
}*/