package com.example.pamplins.apptfg;

import android.content.Intent;
import android.os.Handler;
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

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }, 3000);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    updateUI(mAuth.getCurrentUser(),false);
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "ENTER VALID EMAIL AND PASSWORD", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //TODO ver que si he borrado el usuario. el movil ya tiene guardado y te deja seguir entrando
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(mAuth.getCurrentUser(), true);
    }

    private void updateUI(FirebaseUser currentUser, Boolean start) {
        if(currentUser != null){
            Toast.makeText(getApplicationContext(), "WELCOME!", Toast.LENGTH_LONG).show();
            openHome();
        }else{
            if(!start){
                Toast.makeText(getApplicationContext(), "MAIL AND PASSWORD INCORRECT. TRY AGAIN", Toast.LENGTH_LONG).show();
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
        finish();
    }

}
