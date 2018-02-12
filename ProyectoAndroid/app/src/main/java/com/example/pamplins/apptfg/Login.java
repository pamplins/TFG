package com.example.pamplins.apptfg;
//TODO quitar que se abra el teclado al abrir la App
//TODO hacer que si tocas la pantalla y no es algun campo de texto el teclado desaparezca
import android.content.Intent;
import android.os.Debug;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }

        }, 3000);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    updateUI(mAuth.getCurrentUser(),false);
                }else{
                    Toast.makeText(getApplicationContext(), "Usuario|correo y/o contraseña incorrectos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void signIn(View v) {
        email = etEmail.getText().toString().trim();
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
            if(!start){ // quizas quitar toad y poner error en campos de texto
                Toast.makeText(getApplicationContext(),R.string.err_login, Toast.LENGTH_LONG).show();
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
