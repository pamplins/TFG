package com.example.pamplins.apptfg.View;

/**
 * Created by Gustavo on 03/06/2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    private Timer timer;
    private ImageView splash;
    private static boolean firstTime = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(!firstTime){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firstTime = true;
        }
        startAnimation();
        TimerTask timerTask = new TimerTask()
        {
            public void run()
            {
                if(Utils.hasActiveInternetConnection(SplashActivity.this)){
                    timer.cancel();
                    timer.purge();
                    startSplash();
                }else{
                    Snackbar.make(findViewById(android.R.id.content), R.string.slow_connection, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 1500, 10000);
    }

    private void startAnimation() {
        splash = findViewById(R.id.iv_splash);
        Animation mAnimation = new TranslateAnimation(0, 0, 0, 100);
        mAnimation.setDuration(500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        splash.setAnimation(mAnimation);
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