package com.example.pamplins.apptfg.Controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.example.pamplins.apptfg.Model.Subject;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Comment;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private DatabaseReference answersRef;
    private DatabaseReference subjectsRef;
    private DatabaseReference coursesRef;

    private Controller (){
        initElements();
    }


    private void initElements() {
        db = FirebaseDatabase.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        usersRef = db.getReference(Constants.REF_USERS);
        doubtsRef = db.getReference(Constants.REF_DOUBTS);
        answersRef = db.getReference(Constants.REF_POST_ANSWERS);
        subjectsRef = db.getReference(Constants.REF_SUBJECTS);
        coursesRef = db.getReference(Constants.REF_COURSES);

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

    /**
     * Metodo encargado de reiniciar la instancia una vez
     * el usuario ha cerrado sesion para que luego no haya
     * conflicto cuando vuelva a entrar cogiendo la sesion anterior
     */
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

    public DatabaseReference getCoursesRef() {
        return coursesRef;
    }

    public FirebaseDatabase getDB(){ return db;}

    public DatabaseReference getDoubtReference(String uid){
        return db.getReference()
                .child(Constants.REF_DOUBTS).child(uid);
    }

    public DatabaseReference getUserDoubtReference(String uidAuthor, String uidDoubt){
        return db.getReference()
                .child(Constants.REF_USER_DOUBTS).child(uidAuthor ).child(uidDoubt);
    }

    public DatabaseReference getAnswerReference(String uid){
        return db.getReference()
                .child(Constants.REF_POST_ANSWERS).child(uid);
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Metodo de devolver una consulta a la tabla de usuarios ordenados por
     * el nombre de usuario
     * @return
     */
    public Query getUsersbyUserName(){
        return usersRef.orderByChild(Constants.REF_USERNAME);
    }
    public void uploadImageProfile(String uid, String userName, String email, Bitmap bit, String imagePath, int action) {
        uploadImageProfile(uid, bit, imagePath, userName, email, action);
    }

    /**
     * Metodo encargado de subir la imagen de perfil a storage y
     * añadirsela al usuario. Ademas, actualiza la foto de perfil
     * del usuario si la accion que le llega por parametro no es 0
     *
     * @param uid
     * @param bit
     * @param path
     * @param userName
     * @param email
     * @param action
     */
    private void uploadImageProfile(final String uid, Bitmap bit, String path, final String userName, final String email, final int action) {
        String ref = Constants.REF_PROFILE_IMAGES + uid + "/" + path; // string de la ruta a la que ira
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
                }
                else{
                    //TODO hacer multi-path updates -- INCREMENTO 4
                    updateUserProfileImage(downloadUrl.toString());
                    //usersRef.child(getUid()).child(Constants.REF_PROFILE_NAME).setValue(downloadUrl);

                    // db.child(Constants.REF_USERS).child(uid).child(Constants.REF_PROFILE_NAME).setValue(downloadUrl.toString());

                }
            }
        });
    }

    private void updateUserProfileImage(String downloadUrl) {
        Map<String, Object> updateMap = new HashMap();
        updateMap.put("/"+Constants.REF_USERS+"/"+getUid()+"/"+Constants.REF_PROFILE_NAME, downloadUrl); //users

        // doubts
        for(String ref: user.getUidDoubts()){
            updateMap.put("/"+Constants.REF_DOUBTS+"/"+ref+"/"+Constants.REF_PROFILE_NAME, downloadUrl);
            updateMap.put("/"+Constants.REF_USER_DOUBTS+"/"+getUid()+"/"+ref+"/"+Constants.REF_PROFILE_NAME, downloadUrl);
        }
        // answrs
        String doubtRef;
        List<String> answers = user.getUidAnswers();
        for(int i = 1; i < answers.size(); i+=2){
            doubtRef =answers.get(i-1);
            updateMap.put("/"+Constants.REF_POST_ANSWERS+"/"+doubtRef+"/"+answers.get(i)+"/"+Constants.REF_PROFILE_NAME, downloadUrl);

        }
        db.getReference().updateChildren(updateMap);

    }

    /**
     * Metodo encargado de devolver la fecha para aplicarla en
     * la creacion de una nueva duda o en un comentario
     * @return
     */
    private String getDate(){
        String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        return date+", "+String.format("%02d:%02d", hour, minute);
    }

    /**
     * Metodo encargado de mostrar la imagen de perfil en la duda o
     * comentario que le pertoca
     *
     * @param activity
     * @param urlImageProfile
     * @param img
     */
    public void showProfileImage(Activity activity, String urlImageProfile, ImageView img) {
        Glide.with(activity)
                .load(urlImageProfile)
                .into(img);
    }

    /**
     * Metodo encargado de escribir la duda en base de datos.
     * Si esta duda no contiene ninguna imagen la crea directamente, sino,
     * primero sube las imagenes a storage y luego crea la duda con las urls
     * a estas imagenes
     *
     * @param title
     * @param body
     * @param bits
     * @param ac
     * @param etTitle
     * @param etDescription
     * @param textView
     * @param progressBar
     * @param tvUpload
     * @param tvNewDoubt
     * @param subject
     */
    public void writeDoubtDB(String title, String body, List<Bitmap> bits, Activity ac, EditText etTitle, EditText etDescription, AutoCompleteTextView textView, ProgressBar progressBar, TextView tvUpload, ImageView tvNewDoubt, String subject){
        if(!bits.isEmpty()){
            uploadImagesDoubt(title, body, bits, ac, etTitle, etDescription, textView, progressBar, tvUpload, tvNewDoubt, subject);
        }else{
            uploadNewDoubt(title, body, new ArrayList<String>(), bits, ac, etTitle, etDescription, textView, progressBar, tvUpload, tvNewDoubt, subject);
        }
    }

    /**
     * Metodo encargado de subir las imagenes de la duda y una vez subidas,
     * crear la duda con las ulrs obtenidas de estas
     *
     * @param title
     * @param body
     * @param url1
     * @param ac
     * @param etTitle
     * @param etDescription
     * @param textView
     * @param progressBar
     * @param tvUpload
     * @param tvNewDoubt
     * @param subject
     */
    private void uploadImagesDoubt(final String title, final String body, final List<Bitmap> url1, final Activity ac, final EditText etTitle, final EditText etDescription, final AutoCompleteTextView textView, final ProgressBar progressBar, final TextView tvUpload, final ImageView tvNewDoubt, final String subject) {
        StorageReference fileToUpload;
        final ArrayList<String> finalUrl = new ArrayList<>();
        for(int i = 0; i < url1.size(); i++){
            String ref = Constants.REF_DOUBT_IMAGES + getUid() + "/" + title + "/" + Constants.REF_DOUBT_NAME+"_"+i+".jpg"; // string de la ruta a la que ira
            fileToUpload = storageRef.child(ref);
            Bitmap bit = url1.get(i);
            bit = getVerticalBit(bit);
            final int finalI = i;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();
            fileToUpload.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    finalUrl.add(downloadUrl.toString());
                    if(finalI == url1.size()-1) {
                        uploadNewDoubt(title, body, finalUrl, url1, ac, etTitle, etDescription, textView, progressBar, tvUpload, tvNewDoubt, subject);
                    }
                }
            });
        }
    }

    private Bitmap getVerticalBit(Bitmap bit){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);
    }


    /**
     * Metodo encargado de crear una nueva duda en la tabla de
     * doubts y en la de user doubts. Ademas, limpia todos los
     * elementos utilizados para la creacion de la duda una vez
     * se ha creado esta
     *  @param title
     * @param body
     * @param array
     * @param bits
     * @param ac
     * @param etTitle
     * @param etDescription
     * @param textView
     * @param progressBar
     * @param tvUpload
     * @param tvNewDoubt
     * @param subjectName
     */
    private void uploadNewDoubt(String title, String body, List<String> array, List<Bitmap> bits, final Activity ac, final EditText etTitle, final EditText etDescription, final AutoCompleteTextView textView, final ProgressBar progressBar, final TextView tvUpload, final ImageView tvNewDoubt, final String subjectName){
        final String key = doubtsRef.push().getKey();
        Doubt doubt = new Doubt(getUid(), title, body, getDate(), user.getUserName(), user.getUrlProfileImage(), array, subjectName);
        Map<String, Object> postValues = doubt.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+Constants.REF_DOUBTS+"/" + key, postValues);
        childUpdates.put("/"+Constants.REF_USER_DOUBTS+"/" + doubt.getUid() + "/" + key, postValues);

        db.getReference().updateChildren(childUpdates);

        user.addNewDoubt(key);
        usersRef.child(getUid()).setValue(user);

        addDoubtToSubject(subjectName, key);
        postWriteDoubt(ac, etTitle, etDescription, progressBar, tvUpload, tvNewDoubt, textView);
    }


    public void onLikeClicked(final DatabaseReference postRef, final boolean checkDisLike) {
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


    public void onDisLikeClicked(final DatabaseReference postRef, final boolean checkLike) {
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


    /**
     * Metodo encargado de añadir la duda a la asignatura que le pertoca.
     * Si la asignatura no contiene ninguna duda todavia, la mete en la posicion
     * 0, sino, la añade
     *
     * @param subjectName
     * @param key
     */
    private void addDoubtToSubject(final String subjectName, final String key) {
        getSubjectsRef().child(subjectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Subject subject = dataSnapshot.getValue(Subject.class);
                if(subject.getDoubts().get(0).equals("")){
                    subject.getDoubts().set(0,key);
                }else{
                    subject.addDoubt(key);
                }
                getSubjectsRef().child(subjectName).setValue(subject);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Metodo encargado mostrar que se ha creado la duda correctamente
     * y de limpiar los campos para futuras nuevas dudas
     *
     * @param ac
     * @param etTitle
     * @param etDescription
     * @param progressBar
     * @param tvUpload
     * @param tvNewDoubt
     * @param textView
     */
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

    /**
     * Metodo encargado de escribir la respuesta en una duda en concreto
     *  @param currentdDoubt
     * @param uidDoubt
     * @param etAnswer
     * @param doubtReference
     * @param btnAnswer
     */
    public void writeAnswerDB(final Doubt currentdDoubt, final String uidDoubt, final EditText etAnswer, final DatabaseReference doubtReference, final Button btnAnswer) {
        final String uid = getUid();
        final String answerText = etAnswer.getText().toString();
        if(answerText.trim().isEmpty()){
           etAnswer.setError("Entra comentario");
        }else {
            final String key = answersRef.push().getKey();
            btnAnswer.setEnabled(false); // evitar multiples creaciones de dudas
            Answer answer = new Answer(uid, answerText, getDate(), user.getUserName(), user.getUrlProfileImage(), null);
            Map<String, Object> answerValues = answer.toMap();
            //getAnswerReference(uidDoubt).push().setValue(answerValues);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/"+Constants.REF_POST_ANSWERS+"/"+uidDoubt+"/"+key, answerValues);
            db.getReference().updateChildren(childUpdates);

            user.addNewAnswr(uidDoubt);
            user.addNewAnswr(key);
            usersRef.child(getUid()).setValue(user);

            etAnswer.setText(null);
            currentdDoubt.setnAnswers(currentdDoubt.getnAnswers() + 1);
            doubtReference.child("nAnswers").setValue(currentdDoubt.getnAnswers());
            getUserDoubtReference(currentdDoubt.getUid(), uidDoubt).child("nAnswers").setValue(currentdDoubt.getnAnswers());

            btnAnswer.setEnabled(true);
        }
    }

    /**
     * Metodo encargado de actualizar la lista de Mis asignatura de un usuario.
     * Si no tenia ninguna duda, se mete como primera posicion, sino se añade
     *
     * @param s
     */
    public void updateUserSubjects(String s, boolean add) {
        if(add){
            ArrayList<String> subjects = new ArrayList<>(Arrays.asList(s.substring(1,s.length()-1).split(",")));
            for(String sub : subjects){
                sub = sub.replaceFirst("^ *", "");
                if(user.getSubjects().get(0).equals("")){//TODO quizas hacer esto en user
                    user.getSubjects().set(0,sub);
                }else{
                    if(!user.getSubjects().contains(sub)){
                        user.addNewSubjects(sub);
                    }
                }
            }
        }else {
            user.deleteSubject(s);

        }
        usersRef.child(getUid()).setValue(user);

    }
}