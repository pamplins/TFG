package com.example.pamplins.apptfg;

//TODO hacer que si tocas la pantalla y no es algun campo de texto el teclado desaparezca


import android.content.DialogInterface;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by PAMPLINS on 02/01/2018.
 */

public class Login extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar progressBar;
    private boolean showPass;
    private String email;

    FirebaseAuth mAuth;

    @Override
       protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initElements();
        mAuth = FirebaseAuth.getInstance();
    }


    private void initElements() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        showPassword();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        email = "";
    }

    private void openHome() {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
        finish();
    }


    public void doCheckInputs(){
        String password = etPassword.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    updateUI(mAuth.getCurrentUser(),false);
                    progressBar.setVisibility(View.GONE);
                }else{
                    Snackbar.make(findViewById(android.R.id.content), R.string.err_login, Snackbar.LENGTH_LONG)
                            .show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    public void signIn(View v) {
        email = etEmail.getText().toString().trim();
        if(checkInputs()){
            if(email.contains("@")){
                doCheckInputs();
            }else {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("users").child(email).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            email = dataSnapshot.getValue().toString().trim();
                        }
                        doCheckInputs();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    private boolean checkInputs() {
        if(etEmail.getText().toString().trim().isEmpty()){
            etEmail.setError("Entra el usuario o correo");
        }
        if(etPassword.getText().toString().trim().isEmpty()){
            etPassword.setError("Entra la contraseña");
        }
        if((!etEmail.getText().toString().trim().isEmpty()) && (!etPassword.getText().toString().trim().isEmpty())){
            return true;
        }else{
            return false;
        }

    }

    public void forgotPassword(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.email_registred);
        final EditText email = new EditText(this);
        email.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setHint("Correo");
        alert.setView(email);
        alertSendEmail(alert, email);
        alert.setNegativeButton("Cancelar", null);
        alert.show();

    }

    private void alertSendEmail(AlertDialog.Builder alert, final EditText email) {
        alert.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                if(email.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.err_notSend_email, Snackbar.LENGTH_LONG)
                            .show();
                    progressBar.setVisibility(View.GONE);
                }else{
                    final FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.fetchProvidersForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            if (task.isSuccessful()) {
                                auth.sendPasswordResetEmail(email.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Snackbar.make(findViewById(android.R.id.content), R.string.restartP, Snackbar.LENGTH_LONG)
                                                            .show();
                                                } else {
                                                    Snackbar.make(findViewById(android.R.id.content), R.string.err_send_email, Snackbar.LENGTH_LONG)
                                                            .show();
                                                }
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }else{
                                Snackbar.make(findViewById(android.R.id.content), R.string.email_not_registred, Snackbar.LENGTH_LONG)
                                        .show();
                                progressBar.setVisibility(View.GONE);
                            }
                        };

                    });

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(mAuth.getCurrentUser(), true);
    }

    private void updateUI(FirebaseUser currentUser, Boolean start) {
        if(currentUser != null){
            openHome();
        }else{
            if(!start){
                Snackbar.make(findViewById(android.R.id.content), R.string.err_login, Snackbar.LENGTH_LONG)
                        .show();
            }
        }

    }

    /***
     * Metodo encargado de abrir la ventana de Registro
     *
     * @param v
     */
    public void openRegister(View v){
        Intent i = new Intent(this, Register.class);
        startActivity(i);
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
}
