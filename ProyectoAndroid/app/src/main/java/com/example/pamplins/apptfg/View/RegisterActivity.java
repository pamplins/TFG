package com.example.pamplins.apptfg.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Utils;
import com.example.pamplins.apptfg.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by PAMPLINS on 09/01/2018.
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etUserName;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Bitmap bit;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        initElements();
    }

    private void initElements() {
        etEmail = findViewById(R.id.et_emailR);
        etPassword = findViewById(R.id.et_passwordR);
        showPassword();
        etUserName = findViewById(R.id.et_userName);
        progressBar = findViewById(R.id.progressBarR);
        progressBar.setVisibility(View.INVISIBLE);
    }


    /**
     * Funcion encargada de crear una nueva cuenta de usuario siempre
     * y cuando los valores entrados sean correctos y el usuario a
     * registrar no exista ya en la base de datos
     *
     * @param v
     */
    public void createAccount(final View v){
        progressBar.setVisibility(View.VISIBLE);
        String name = etUserName.getText().toString().trim();
        if(Utils.isNetworkAvailable(this)) {
            if (checkInputs()) {
                etEmail.setEnabled(false);
                etPassword.setEnabled(false);
                etUserName.setEnabled(false);
                v.setEnabled(false);
                DatabaseReference users = FirebaseDatabase.getInstance().getReference(Constants.REF_USERS);
                users.orderByChild(Constants.REF_USERNAME).equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                updateUI(user);
                                                enableElements(v);
                                            } else {
                                                etEmail.setError(getString(R.string.err_email_exist));
                                                enableElements(v);

                                            }
                                        }
                                    });
                        } else {
                            etUserName.setError(getString(R.string.err_user_exist));
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }else{
            Snackbar.make(findViewById(android.R.id.content), R.string.err_conex, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void enableElements(View v) {
        progressBar.setVisibility(View.INVISIBLE);
        v.setEnabled(true);
        etEmail.setEnabled(true);
        etPassword.setEnabled(true);
        etUserName.setEnabled(true);
    }

    /**
     * Metodo encargado de comprobar los diferentes campos de texto
     * introducidos y mostrar error si son incorrectos
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
     * Funcion encargada de validar el nombre de usuario, si este
     * tiene una longitud mayor a 3 se da por valido
     *
     * @return
     */
    private boolean userNameValidator() {
        return etUserName.getText().toString().trim().length() > 3;
    }

    /**
     * Funcion encargada de validar la contraseña, si esta tiene una
     * longitud mayor a 5
     *
     * @return
     */
    private boolean passwordValidator() {
        return etPassword.getText().toString().trim().length() > 5;
    }


    /**
     * Funcion encargada de validar el correo mediante una regex que
     * confirma que sigue el modelo ejemplo@ejemplo.dominio
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
     * Metodo encargado mostrar la pantalla principal de la aplicacion y
     * cerrar la de registro una vez el usuario creado tiene la foto de perfil
     *
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        if(user != null){
            uploadImageProfile(user);
            openHome();
            finish();
        }
    }

    /**
     * Funcion encargada de enviar a controlador el usuario registrado
     * para asi añadir la imagen de perfil en el controlador
     *
     * @param user
     */
    private void uploadImageProfile(FirebaseUser user) {
        String userName = etUserName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        if(null == bit){
            bit = BitmapFactory.decodeResource(getResources(),R.drawable.user_default);
        }
        Controller.getInstance().uploadImageProfile(user.getUid(), userName, email, bit, "image_profile.jpg", 0);
    }


    private void openHome() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Funcion encargada de llamar a la funcion openAlert
     * si los permisos de camara y de leer storage estan activos
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
     * Funcion encargada de abrir el dialogo para escoger entre la
     * galeria o camara para subir una imagen
     */
    private void openAlert() {
        final CharSequence[] items = {getResources().getString(R.string.open_camera), getResources().getString(R.string.select_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(R.string.add_image);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals(getResources().getString(R.string.open_camera))) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePictureIntent, 0);

                    } else if (items[item].equals(getResources().getString(R.string.select_gallery))){

                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1);
                    }
                }

            });
            builder.show();
    }

    /**
     * Metodo encargado de obtener la imagen que el usuario
     * ha seleccionado, ya sea desde camara o desde storage
     *
     * @param requestCode
     * @param resultCode
     * @param imageReturnedIntent
     */
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


    private void showPassword() {
        Utils.showPassword(etPassword);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void hideKeyboard(View v){
       Utils.hideKeyboard(this);
    }


}