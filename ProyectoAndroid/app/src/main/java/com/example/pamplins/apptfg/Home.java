package com.example.pamplins.apptfg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
/**
 * Created by PAMPLINS on 02/01/2018.
 */

public class Home extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signOut(View v){
        mAuth.signOut();
        Intent i = new Intent(this, Login.class);
        startActivity(i);
        finish();
    }

    //                 mAuth.signOut();
  // hacer esto en un boton para exit

}
