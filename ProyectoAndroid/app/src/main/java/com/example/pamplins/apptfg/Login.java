package com.example.pamplins.apptfg;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by PAMPLINS on 02/01/2018.
 */

public class Login extends AppCompatActivity {

    private TextView tvEmail;
    private TextView tvPassword;
    private Button btnSignin;
    private Button btnRegister;
    private ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
       protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initElements();
        mAuth = FirebaseAuth.getInstance();
    }


    private void initElements() {
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvPassword = (TextView) findViewById(R.id.tv_password);
        btnSignin = (Button) findViewById(R.id.btn_signin);
        btnRegister = (Button) findViewById(R.id.btn_register);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void openHome() {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
        finish();
    }


    public void signIn(View v){
        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();
        if(!email.isEmpty() && !password.isEmpty()){
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    updateUI(currentUser);
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "ENTER EMAIL AND PASSWORD BOY", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Toast.makeText(getApplicationContext(), "CORRECT", Toast.LENGTH_LONG).show();
            openHome();
        }else{
            btnRegister.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);

    }

    public void openRegister(View v){
        Intent i = new Intent(this, Register.class);
        startActivity(i);
        finish();
    }



}
