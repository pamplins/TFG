package com.example.pamplins.apptfg;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private TextView tvCPassword;
    private TextView tvPseudo;
    private Button btnSignin;
    private Spinner spinner;
    FirebaseAuth mAuth;
    private boolean showPass;

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

        tvPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (tvPassword.getRight() - tvPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!showPass){
                            tvPassword.setTransformationMethod(new PasswordTransformationMethod());
                            showPass = true;
                        }else {
                            tvPassword.setTransformationMethod(null);
                            showPass = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        //tvCPassword = (TextView) findViewById(R.id.tv_cpasswordR);
        tvPseudo = (TextView) findViewById(R.id.tv_pseudo);
        btnSignin = (Button) findViewById(R.id.btn_registerR);
        spinner = (Spinner) findViewById(R.id.spinner); //TODO hacer spinner correctamente y hacer custom simple_spinner_dropdown
        String[] curs = {"                          Curs actual","1r","2n","3r","4rt"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, curs));
        showPass = false;
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
                                Toast.makeText(Register.this, "USER ALREADY REGISTERED",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }else{
            updateUI(null);
        }
    }

    private boolean checkInputs() {
        // TODO mirar si pseudo ya esta cogido y lo del curso
        tvsEmpties();
        if((emailValidator()) && (passwordValidator())){
            return true;
        }
        return false;
    }

    private void tvsEmpties() {
        if(tvPseudo.getText().toString().isEmpty()){
            tvPseudo.setError("Input empty");
        }
        if(tvEmail.getText().toString().isEmpty()){
            tvEmail.setError("Input empty");
        }if(tvPassword.getText().toString().isEmpty()){
            tvPassword.setError("Input empty");

        }if(tvCPassword.getText().toString().isEmpty()){
            tvCPassword.setError("Input empty");
        }if(spinner.getSelectedItem().toString().equals("                          Curs actual")){
        //TODO ver como mostrar error
        }

    }

    private boolean passwordValidator() {
        String passsword = tvPassword.getText().toString();

        if(passsword.length() > 6){
            return true;
        }else{
            return false;
        }
    }

    /*
    private boolean passwordValidator() {
        String passsword = tvPassword.getText().toString();
        String cpasssword = tvCPassword.getText().toString();
        if(passsword.length() > 6 && cpasssword.length() > 6){
            if(!passsword.equals(cpasssword)){
                tvCPassword.setError("Confirmation password error");
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }

    }*/

    /**
     * validate your email address format. Ex-akhi@mani.com
     */
    private boolean emailValidator() {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(this.tvEmail.getText().toString());
        return matcher.matches();
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            Toast.makeText(getApplicationContext(), "CORRECT REGISTER. WELCOME", Toast.LENGTH_LONG).show();
            openHome();
        }
    }

    private void openHome() {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
        finish();
    }


    //CUANDO VOLQUEMOS LOS USUARIOS A LA BASE DE DATOS para saber si ya esta el nombre del user. se cambiara la forma de login seguramente
    /*
    DatabaseReference ref = Firebase.getInstance().getReference();
    ref.child("users").child("username").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                // use "username" already exists
                // Let the user know he needs to pick another username.
            } else {
                // User does not exist. NOW call createUserWithEmailAndPassword
                mAuth.createUserWithPassword(...);
                // Your previous code here.

            }
        }*/
}
