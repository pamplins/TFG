package com.example.pamplins.apptfg.View;


import android.content.DialogInterface;
import android.content.Intent;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Utils;
import com.example.pamplins.apptfg.R;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by PAMPLINS on 09/01/2018.
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etUserName;
    private Spinner spinner;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Bitmap bit;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initElements();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Metodo encargado de inicializar los elementos
     */
    private void initElements() {
        etEmail = findViewById(R.id.et_emailR);
        etPassword = findViewById(R.id.et_passwordR);
        showPassword();
        etUserName = findViewById(R.id.et_userName);
        //initSpinner();
        progressBar = findViewById(R.id.progressBarR);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Funcion encargada de inicializar el spinner para escoger el curso
     */
    private void initSpinner() {
        //spinner = findViewById(R.id.spinner);
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

    /**
     * Metodo encargado de mostrar la contraseña o ocultarla si se clica sobre la imagen del EditText
     */
    private void showPassword() {
        Utils.showPassword(etPassword);
    }

    /**
     * Funcion encargada de crear una nueva cuenta de usuario
     *
     * @param v
     */
    public void createAccount(final View v){
        progressBar.setVisibility(View.VISIBLE);
        String name = etUserName.getText().toString().trim();
        if (checkInputs()) {
            v.setEnabled(false);
            DatabaseReference users = FirebaseDatabase.getInstance().getReference(Constants.REF_USERS);
            users.orderByChild(Constants.REF_USERNAME).equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     if(!dataSnapshot.exists()){
                         mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                             .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if (task.isSuccessful()) {
                                         // Sign in success, update UI with the signed-in user's information
                                         FirebaseUser user = mAuth.getCurrentUser();
                                         updateUI(user);
                                         progressBar.setVisibility(View.INVISIBLE);
                                         v.setEnabled(true);
                                     } else {
                                         etEmail.setError(getString(R.string.err_email_exist));
                                         progressBar.setVisibility(View.INVISIBLE);
                                         v.setEnabled(true);

                                     }
                                 }
                             });
                     }
                     else{
                         etUserName.setError(getString(R.string.err_user_exist));
                         progressBar.setVisibility(View.INVISIBLE);
                     }
                 }
                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }

                });

        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Funcion encargade de comprobar los diferentes campos de texto y el spinner
     *
     * @return
     */
    private boolean checkInputs() {
        boolean c_userN = userNameValidator();
        boolean c_email = emailValidator();
        boolean c_pass = passwordValidator();
        if(!c_userN){
            etUserName.setError(getString(R.string.err_userName_len));
        }
        if(!c_email){
            etEmail.setError(getString(R.string.err_email_format));
        }
        if(!c_pass){
            etPassword.setError(getString(R.string.err_pass_len));
        }
        if(c_userN && c_email && c_pass){
            return true;
        }
        return false;
    }
    /**
     * Funcion encargada de validar el nombre de usuario
     *
     * @return
     */
    private boolean userNameValidator() {
        return etUserName.getText().toString().trim().length() > 3;
    }

    /**
     * Funcion encargada de validar la contraseña
     *
     * @return
     */
    private boolean passwordValidator() {
        return etPassword.getText().toString().trim().length() > 5;
    }


    /**
     * Funcion encargada de validar el correo
     */
    private boolean emailValidator() {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(etEmail.getText().toString().trim());
        return matcher.matches();
    }

    /**
     * Metodo encargado de recargar la interfaz de usuario
     *
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        if(user != null){
            writeUserDB(user);
            openHome();
            finish();
        }
    }

    /**
     * Funcion encargada de añadir a la base de datos el nuevo usuario registrado
     *
     * @param user
     */
    private void writeUserDB(FirebaseUser user) {
        String userName = etUserName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        //String spinnerItem = spinner.getSelectedItem().toString();
        if(null == bit){
            bit = BitmapFactory.decodeResource(getResources(),R.drawable.user_default);
        }
        Controller.getInstance().writeUserDB(user.getUid(), userName, email, bit, "image_profile.jpg", 0);
        //UtilsPassword.uploadImageProfile(user.getUid(), bit, "image_profile.jpg");
    }

    /**
     * Funcion encargada de abrir la pantalla de home
     */
    private void openHome() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Funcion encargada de
     *
     * @param v
     */
    public void openImage(View v){
        if(Utils.verifyPermissions(this)) {
            openAlert();
        }else{
            Snackbar.make(findViewById(android.R.id.content), R.string.permiss, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Funcion encargada de abrir el dialogo para escoger entre la galeria o camara para subir una imagen
     */
    private void openAlert() {
        final CharSequence[] items = {"Hacer foto", "Seleccionar de galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
        if (resultCode == RESULT_OK) {
            if(requestCode == 0){
                bit = (Bitmap) imageReturnedIntent.getExtras().get("data");
                ImageView img = findViewById(R.id.img_user);
                img.setDrawingCacheEnabled(true);
                img.buildDrawingCache();
                img.setImageBitmap(bit);
            }else{
                try {
                    Uri uri = imageReturnedIntent.getData();
                    InputStream imageStream = getContentResolver().openInputStream(uri);
                    bit = BitmapFactory.decodeStream(imageStream);
                    ImageView img = findViewById(R.id.img_user);
                    img.setDrawingCacheEnabled(true);
                    img.buildDrawingCache();
                    img.setImageBitmap(bit);
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


    public void hideKeyboard(View v){
       Utils.hideKeyboard(this);
    }


}