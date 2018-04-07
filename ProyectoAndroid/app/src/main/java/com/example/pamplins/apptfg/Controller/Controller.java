package com.example.pamplins.apptfg.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.DoubtDetailActivity;
import com.example.pamplins.apptfg.Model.Answer;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
                    db.child(Constants.REF_USERS).child(uid).child(Constants.REF_PROFILE_NAME).setValue(downloadUrl.toString());
                    db.child(Constants.REF_USERS).child(uid).child(Constants.REF_PROFILE_NAME).setValue(downloadUrl.toString());

                }
            }
        });
    }


    public String getDate(){
        String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        date = date+", "+String.format("%02d:%02d", hour, minute);

        return date;
    }

    public void showProfileImage(Activity activity, Object obj, ImageView img) {
        String url;
        if(obj.getClass().equals(Doubt.class)){
            url = ((Doubt)obj).getUser().getUrlProfileImage();
        }else{
            url = ((Answer)obj).getUser().getUrlProfileImage();
        }
        Glide.with(activity)
                .load(url)
                .into(img);
    }


    public void writeDoubtDB(String userId, String title, String body, User user, ArrayList<String> url1, ArrayList<String> url2, Button btn, EditText et1, EditText et2, RecyclerView rv, Activity activity, TextView tvUpload){
        if(!url1.isEmpty()){
            uploadImagesDoubt(userId, Constants.REF_DOUBT_IMAGES, Constants.REF_DOUBT_NAME, title, body, user, url1, url2, btn, et1, et2, rv, activity, tvUpload);
        }else{
            uploadNewDoubt(userId, title, body, user, url2, btn, et1, et2, rv, url1, activity, tvUpload);
        }
    }

    private void uploadImagesDoubt(final String uid, String folder, final String path, final String title, final String body, final User user, final ArrayList<String> url1, ArrayList<String> url2, final Button btn, final EditText et1, final EditText et2, final RecyclerView rv, final Activity activity, final TextView tvUpload) {
        for(int i = 0; i < url1.size(); i++){
            String ref = folder + uid + "/" + title + "/" + path+"_"+i+".jpg"; // string de la ruta a la que ira
            StorageReference fileToUpload;
            fileToUpload = FirebaseStorage.getInstance().getReference().child(ref);

            final int finalI = i;
            final ArrayList<String> finalUrl = url2;
            fileToUpload.putFile(Uri.parse(url1.get(i))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    finalUrl.add(downloadUrl.toString());
                    if(finalI == url1.size()-1) {
                        uploadNewDoubt(uid, title, body, user, finalUrl, btn, et1, et2, rv, url1, activity, tvUpload);
                    }
                }
            });
        }
    }

    private void uploadNewDoubt(String userId, String title, String body, User user, ArrayList array, Button btn, EditText et1, EditText et2, RecyclerView rv, ArrayList<String> url1, Activity activity, TextView tvUpload){
        String key = FirebaseDatabase.getInstance().getReference().child(Constants.REF_DOUBTS).push().getKey();
        Doubt doubt = new Doubt(userId, title, body, ctrl.getDate(), user, array);
        Map<String, Object> postValues = doubt.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+Constants.REF_DOUBTS+"/" + key, postValues);
        childUpdates.put("/"+Constants.REF_USER_DOUBTS+"/" + doubt.getUid() + "/" + key, postValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
        Snackbar.make(activity.findViewById(android.R.id.content), "Tu duda se ha posteado correctamente", Snackbar.LENGTH_LONG)
                .show();
        btn.setEnabled(true);
        et1.setEnabled(true);
        et2.setEnabled(true);
        tvUpload.setEnabled(true);
        et1.setText("");
        et2.setText("");
        if (!url1.isEmpty()) {
            url1.clear();
            rv.getAdapter().notifyDataSetChanged();
        }
    }

    public void writeAnswerDB(final Doubt currentdDoubt, final DatabaseReference answersReference, final EditText etAnswer, final DatabaseReference doubtReference, final Button btnAnswer, final DoubtDetailActivity activity) {
        final String uid = getUid();
        final String answerText = etAnswer.getText().toString();
        if(answerText.trim().isEmpty()){
            etAnswer.setError("Entra comentario");
        }else {
            btnAnswer.setEnabled(false); // evitar multiples creaciones de dudas
            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final User user = dataSnapshot.getValue(User.class);
                            final String authorName = user.getUserName();
                            //TODO se puede mejorar el codigo
                            getUsersbyUserName().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        User user = childSnapshot.getValue(User.class);
                                        if (user.getUserName().equals(authorName)) {
                                            Answer answer = new Answer(uid, answerText, ctrl.getDate(), user);
                                            Map<String, Object> answerValues = answer.toMap();
                                            answersReference.push().setValue(answerValues);
                                            etAnswer.setText(null);
                                            currentdDoubt.setnAnswers(currentdDoubt.getnAnswers() + 1);
                                            doubtReference.child("nAnswers").setValue(currentdDoubt.getnAnswers());
                                            ctrl.hideKeyboard(activity);
                                            btnAnswer.setEnabled(true);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    btnAnswer.setEnabled(true);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            btnAnswer.setEnabled(true);
                        }
                    });
        }
    }
}