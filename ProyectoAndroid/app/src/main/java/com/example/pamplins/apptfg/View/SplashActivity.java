package com.example.pamplins.apptfg.View;

/**
 * Created by Gustavo on 03/06/2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startSplash();
    }

    private void startSplash() {
         new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    while(Controller.getInstance().getUser() == null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }.execute();


    }
}