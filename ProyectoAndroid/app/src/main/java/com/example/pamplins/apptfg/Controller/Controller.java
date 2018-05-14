package com.example.pamplins.apptfg.Controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Utils;
import com.example.pamplins.apptfg.View.DoubtDetailActivity;
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
import java.util.Arrays;
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
    private User user;
    private FirebaseDatabase db;
    private StorageReference storageRef;
    private DatabaseReference usersRef;
    private DatabaseReference doubtsRef;
    private DatabaseReference subjectsRef;
    private Controller (){
        initElements();
    }


    private void initElements() {
        db = FirebaseDatabase.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        usersRef = db.getReference(Constants.REF_USERS);
        doubtsRef = db.getReference(Constants.REF_DOUBTS);
        subjectsRef = db.getReference(Constants.REF_SUBJECTS);

        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static Controller getInstance(){
        if(ctrl == null){
            ctrl = new Controller();
        }
        return ctrl;
    }

    public void restartInstance(){
        ctrl = null;
    }

    public StorageReference getStorageRef(){
        return storageRef;
    }
    public User getUser() {
        return user;
    }

    public DatabaseReference getUsersRef() {
        return usersRef;
    }

    public DatabaseReference getDoubtsRef() {
        return doubtsRef;
    }

    public DatabaseReference getSubjectsRef() {
        return subjectsRef;
    }



    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    public Query getUsersbyUserName(){
        return usersRef.orderByChild(Constants.REF_USERNAME);
    }

    public void writeUserDB(String uid, String userName, String email, Bitmap bit, String imagePath, int action) {
        uploadImageProfile(uid, bit, Constants.REF_PROFILE_IMAGES, imagePath, userName, email, action);

    }



    public void uploadImageProfile(final String uid, Bitmap bit, String folder, String path, final String userName, final String email, final int action) {
        String ref = folder + uid + "/" + path; // string de la ruta a la que ira
        StorageReference mStorageRef;
        mStorageRef =  storageRef.child(ref);
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
                    User user = new User(userName, email, downloadUrl.toString());
                    usersRef.child(uid).setValue(user);
                }// Si no es 0, significa que sube foto de comentario o duda
                else{
                    //TODO hacer multi-path updates -- ITERACION 5
                    usersRef.child(uid).child(Constants.REF_PROFILE_NAME).setValue(downloadUrl.toString());
                   // db.child(Constants.REF_USERS).child(uid).child(Constants.REF_PROFILE_NAME).setValue(downloadUrl.toString());

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


    public void writeDoubtDB(String title, String body, ArrayList<String> url1, ArrayList<String> url2, RecyclerView rv, Activity ac, EditText etTitle, EditText etDescription, AutoCompleteTextView textView, ProgressBar progressBar, TextView tvUpload, ImageView tvNewDoubt, String subject){
        if(!url1.isEmpty()){
            uploadImagesDoubt(title, body, user, url1, url2,rv, ac, etTitle, etDescription, textView, progressBar, tvUpload, tvNewDoubt, subject);
        }else{
            uploadNewDoubt(title, body, user, url2, rv, url1, ac, etTitle, etDescription, textView, progressBar, tvUpload, tvNewDoubt, subject);
        }
    }

    private void uploadImagesDoubt(final String title, final String body, final User user, final ArrayList<String> url1, ArrayList<String> url2, final RecyclerView rv, final Activity ac, final EditText etTitle, final EditText etDescription, final AutoCompleteTextView textView, final ProgressBar progressBar, final TextView tvUpload, final ImageView tvNewDoubt, final String subject) {
        for(int i = 0; i < url1.size(); i++){
            String ref = Constants.REF_DOUBT_IMAGES + getUid() + "/" + title + "/" + Constants.REF_DOUBT_NAME+"_"+i+".jpg"; // string de la ruta a la que ira
            StorageReference fileToUpload;
            fileToUpload = storageRef.child(ref);

            final int finalI = i;
            final ArrayList<String> finalUrl = url2;
            fileToUpload.putFile(Uri.parse(url1.get(i))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    finalUrl.add(downloadUrl.toString());
                    if(finalI == url1.size()-1) {
                        uploadNewDoubt(title, body, user, finalUrl, rv, url1, ac, etTitle, etDescription, textView, progressBar, tvUpload, tvNewDoubt, subject);

                    }
                }
            });
        }
    }

    private void uploadNewDoubt(String title, String body, User user, ArrayList array, RecyclerView rv, ArrayList<String> url1, Activity ac, EditText etTitle, EditText etDescription, AutoCompleteTextView textView, ProgressBar progressBar, TextView tvUpload, ImageView tvNewDoubt, String subject){
        String key = doubtsRef.push().getKey();

        // TODO optimizar esto

        Doubt doubt = new Doubt(getUid(), title, body, ctrl.getDate(), user, array, subject);
        Map<String, Object> postValues = doubt.toMap();

       /* Map<String, Object> childUpdates2 = new HashMap<>();
            childUpdates2.put("/"+Constants.REF_COURSE+"/first/first_semester/algebra/"+key, doubt.getUid());
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates2);*/


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+Constants.REF_DOUBTS+"/" + key, postValues);
        childUpdates.put("/"+Constants.REF_USER_DOUBTS+"/" + doubt.getUid() + "/" + key, postValues);
        db.getReference().updateChildren(childUpdates);
        if (!url1.isEmpty()) {
            url1.clear();
            rv.getAdapter().notifyDataSetChanged();
        }

        postWriteDoubt(ac, etTitle, etDescription, progressBar, tvUpload, tvNewDoubt, textView);
    }

    private void postWriteDoubt(Activity ac, EditText etTitle, EditText etDescription, ProgressBar progressBar, TextView tvUpload, ImageView tvNewDoubt, AutoCompleteTextView textView) {
       Snackbar.make(ac.findViewById(android.R.id.content), "Tu duda se ha creado correctamente", Snackbar.LENGTH_LONG)
                .show();
        ac.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);
        etTitle.setEnabled(true);
        etDescription.setEnabled(true);
        tvUpload.setEnabled(true);
        etTitle.setText("");
        etDescription.setText("");
        textView.setEnabled(true);
        textView.setText("");
        tvNewDoubt.setEnabled(true);
    }

    public void writeAnswerDB(final Doubt currentdDoubt, final DatabaseReference answersReference, final EditText etAnswer, final DatabaseReference doubtReference, final Button btnAnswer, final DoubtDetailActivity activity) {
        final String uid = getUid();
        final String answerText = etAnswer.getText().toString();
        if(answerText.trim().isEmpty()){
           etAnswer.setError("Entra comentario");
        }else {
            btnAnswer.setEnabled(false); // evitar multiples creaciones de dudas
            Answer answer = new Answer(uid, answerText, ctrl.getDate(), user);
            Map<String, Object> answerValues = answer.toMap();
            answersReference.push().setValue(answerValues);
            etAnswer.setText(null);
            currentdDoubt.setnAnswers(currentdDoubt.getnAnswers() + 1);
            doubtReference.child("nAnswers").setValue(currentdDoubt.getnAnswers());
            Utils.hideKeyboard(activity);
            btnAnswer.setEnabled(true);
        }
    }

    public void updateUser(String s) {
        ArrayList<String> subjects = new ArrayList<>(Arrays.asList(s.substring(1,s.length()-1).split(",")));

        ctrl.getUser().setSubjects(subjects);
        usersRef.child(getUid()).setValue(ctrl.getUser());
    }
}