package com.example.pamplins.apptfg;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by PAMPLINS on 09/01/2018.
 */

public class Register extends AppCompatActivity {


    private TextView tvEmail;
    private TextView tvPassword;
    private Button btnSignin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initElements();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initElements() {
        tvEmail = (TextView) findViewById(R.id.tv_emailR);
        tvPassword = (TextView) findViewById(R.id.tv_passwordR);
        btnSignin = (Button) findViewById(R.id.btn_registerR);
        //TODO hacer otro tvPassword y comprobar que existe
    }



    public void createAccount(View v){
        if(checkInputs()) {
            mAuth.createUserWithEmailAndPassword(tvEmail.getText().toString(), tvPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }else{ // usuario ya existe, limitar a este error para
                                Toast.makeText(Register.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }else{
            Toast.makeText(Register.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }

    private boolean checkInputs() {
        // TODO isEmpty no, poner una funcion que compruebe las dos passwords y que no sea Empty ninguna de las 2
        if((emailValidator(tvEmail.getText().toString())) && !(tvPassword.getText().toString().isEmpty())){
            return true;
        }
        return false;
    }

    /**
     * validate your email address format. Ex-akhi@mani.com
     */
    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            Toast.makeText(getApplicationContext(), "CORRECT", Toast.LENGTH_LONG).show();
            openHome();
        }
    }

    private void openHome() {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
        finish();
    }
}
