package com.example.pamplins.apptfg;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by gtenorio on 05/02/2018.
 */

public class Utils {
    private static boolean showPass = false;

    /**
     * Metodo encargado de mostrar u ocultar la contraseña si
     * se hace clic sobre el ojo del EditText correspondiente
     * @param etPassword
     */
    public static void showPassword(final EditText etPassword) {
        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(showPass){
                            etPassword.setSelection(etPassword.length());
                            etPassword.setTransformationMethod(new PasswordTransformationMethod());
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
                            showPass = false;
                        }else {
                            etPassword.setSelection(etPassword.length());
                            etPassword.setTransformationMethod(null);
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye2, 0);
                            showPass = true;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static boolean hasActiveInternetConnection(Activity activity) {
        if (isNetworkAvailable(activity)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
            } catch (IOException e) {
            }
        } else {
        }
        return false;
    }
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Metodo encargado de esconder el teclado
     * si se presiona sobre otra parte de la actividad
     * @param activity
     */
    public static void hideKeyboard(Activity activity){
        try{
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e){

        }

    }

    /**
     * Metodo encargado de verificar si los permisos de camara
     * y lectura del storage ya han sido aceptados en la aplicacion
     * @param activity
     * @return
     */
    public static boolean verifyPermissions(Activity activity) {
        int p_camera = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA);
        int p_storage = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if((p_camera == PackageManager.PERMISSION_GRANTED) && (p_storage == PackageManager.PERMISSION_GRANTED)){
            return true;
        }else{
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
            return false;
        }
    }

    public static AlphaAnimation getButtonAnimation(){
        AlphaAnimation buttonAnimation = new AlphaAnimation(0.2f, 1.0f);
        buttonAnimation.setDuration(500);
        return buttonAnimation;
    }

}