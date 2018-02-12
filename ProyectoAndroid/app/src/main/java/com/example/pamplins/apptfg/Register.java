package com.example.pamplins.apptfg;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by PAMPLINS on 09/01/2018.
 */

public class Register extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etUserName;
    private Spinner spinner;
    FirebaseAuth mAuth;
    private boolean showPass;
    private boolean freeUserName;
    private ProgressBar progressBar;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initElements();
        mAuth = FirebaseAuth.getInstance();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initElements() {
        etEmail = findViewById(R.id.et_emailR);
        etPassword = findViewById(R.id.et_passwordR);
        showPassword();
        etUserName = findViewById(R.id.et_userName);
        initSpinner();
        showPass = false;
        freeUserName = false;
        progressBar = findViewById(R.id.progressBarR);
        progressBar.setVisibility(View.INVISIBLE);
/*
        etEmail.setText("gus17@gmail.com");
        etPassword.setText("quecontra");
        etUserName.setText("gus17");*/
    }

    private void initSpinner() {
        spinner = findViewById(R.id.spinner);
        String[] courses = new String[]{"Curso actual","1º","2º","3º","4º"};
        final List<String> courseList = new ArrayList<>(Arrays.asList(courses));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,courseList){
            @Override
            public boolean isEnabled(int position){
                return position == 0 ? false : true;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                int color = position == 0 ? Color.GRAY : Color.WHITE;
                tv.setTextColor(color);
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }


    private void showPassword() {
        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!showPass){
                            etPassword.setSelection(etPassword.length());
                            etPassword.setTransformationMethod(new PasswordTransformationMethod());
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
                            showPass = true;
                        }else {
                            etPassword.setSelection(etPassword.length());
                            etPassword.setTransformationMethod(null);
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye2, 0);
                            showPass = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void createAccount(View v){
        if(checkInputs()) {
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }, 3000);
            mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }else{
                                etEmail.setError(getString(R.string.err_email_exist));
                            }

                        }
                    });
        }else{
            updateUI(null);
        }
    }

    private boolean checkInputs() {
        checkDB();
        if(!userNameValidator()){ // Mostrare error por campo vacio, porque el usuario ya existe
            etUserName.setError(getString(R.string.err_userName_len));
        }
        if(!emailValidator()){
            etEmail.setError(getString(R.string.err_email_format));
        }
        if(!passwordValidator()){
            etPassword.setError(getString(R.string.err_pass_len));
        }
        if(cursValidator()){
            ((TextView)spinner.getSelectedView()).setError(getString(R.string.err_course));
        }
        if(userNameValidator() && emailValidator() && passwordValidator() && !cursValidator() && freeUserName){
            return true;
        }
        return false;
    }

    private void checkDB() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final String name = etUserName.getText().toString().trim();
        ref.child("users").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    freeUserName  = true;
                }else{
                    etUserName.setError(getString(R.string.err_user_exist));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private boolean cursValidator() {
        return (spinner.getSelectedItem().toString().equals("Curso actual"));
    }

    private boolean userNameValidator() {
        return etUserName.getText().toString().trim().length() > 3;
    }

    private boolean passwordValidator() {
        return etPassword.getText().toString().trim().length() > 5;
    }


    /**
     * validate your email address format. Ex-aa1@msn.com
     */
    private boolean emailValidator() {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(etEmail.getText().toString().trim());
        return matcher.matches();
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            writeUserDB(user);
            openHome();
            finish();
        }
    }

    private void writeUserDB(FirebaseUser uid) {
        String userName = etUserName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String spinnerItem = spinner.getSelectedItem().toString();

        User user = new User(uid.getUid(), userName, email, spinnerItem);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("users").child(userName).setValue(user.toDictionary());
        if(null == uri){
            uri = Uri.parse("android.resource://com.example.pamplins.apptfg/" + R.drawable.user_default);
        }
        ImageUtils.uploadImageProfile(userName, uri, "aux_image.jpg", db);
    }

    private void openHome() {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void openImage(View v){
        final CharSequence[] items = {"Hacer foto", "Seleccionar de galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setTitle("Añadir imagen");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Hacer foto")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, 0);
                } else if (items[item].equals("Seleccionar de galeria")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }

        });
        builder.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        //TODO coger URI desde camara y y pedir permisos
        if (resultCode == RESULT_OK) {
            if(requestCode == 0){
                Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                ImageView img = findViewById(R.id.img_user);
                img.setImageBitmap(ImageUtils.getCircularBitmap(bitmap));
            }else{
                try {
                    uri = imageReturnedIntent.getData();
                    Log.d("CAMERA", uri.toString());

                    InputStream imageStream = getContentResolver().openInputStream(uri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ImageView img = findViewById(R.id.img_user);
                    img.setImageBitmap(ImageUtils.getCircularBitmap(selectedImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    @Override
    public void onBackPressed() {
        finish();
    }

}