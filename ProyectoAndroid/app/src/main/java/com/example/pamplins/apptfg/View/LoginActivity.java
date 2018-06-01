package com.example.pamplins.apptfg.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.User;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String email;

    @Override
       protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initElements();
    }

    private void initElements() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        showPassword();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        email = "";
    }

    /**
     * Metodo encargado de llamar a updateUI cada vez que se abre la aplicacion
     * para asi evitar entrar los datos de iniciar sesion si ya habia iniciado
     * sesion anteriormente en el dispositivo movil
     */
    @Override
    protected void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser(), true);
    }

    private void openHome() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Metodo encargado de comprobar si el inicio de sesion
     * se hace mediante usuario o correo. Si es por nombre de
     * usuario, obtiene el objeto usuario desde base de datos
     * para asi extraer el correo electronico y de esta forma
     * iniciar sesion en el metodo authentication
     *
     * @param v
     */
    public void signIn(final View v) {
        email = etEmail.getText().toString().trim();
        if(Utils.isNetworkAvailable(this)) {
            if (checkInputs()) {
                v.setEnabled(false);
                if (email.contains("@")) {
                    authentication(v);
                } else {
                    DatabaseReference users = FirebaseDatabase.getInstance().getReference(Constants.REF_USERS);
                    users.orderByChild(Constants.REF_USERNAME).equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = null;
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    user = childSnapshot.getValue(User.class);
                                }
                                email = user.getEmail();
                            }
                            authentication(v);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            v.setEnabled(true);
                        }
                    });
                }
            }
        }else{
            Snackbar.make(findViewById(android.R.id.content), R.string.err_conex, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    /***
     * Metodo encargado de autentificar que el correo y la contraseña son
     * de un usuario existente en base de datos
     * @param v
     */
    public void authentication(final View v){
        String password = etPassword.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    updateUI(mAuth.getCurrentUser(),false);
                    progressBar.setVisibility(View.GONE);
                    v.setEnabled(true);
                }else{
                    Snackbar.make(findViewById(android.R.id.content), R.string.err_login, Snackbar.LENGTH_LONG)
                            .show();
                    progressBar.setVisibility(View.GONE);
                    v.setEnabled(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(findViewById(android.R.id.content), R.string.err_conex, Snackbar.LENGTH_LONG)
                        .show();
                progressBar.setVisibility(View.GONE);
                v.setEnabled(true);
            }
        });
    }

    /**
     * Metodo encargado de comprobar los diferentes campos de texto
     * introducidos y mostrar error si son incorrectos
     *
     * @return
     */
    private boolean checkInputs() {
        boolean c_email = etEmail.getText().toString().trim().isEmpty();
        boolean c_pass = etPassword.getText().toString().trim().isEmpty();
        if(c_email){
            etEmail.setError(getResources().getString(R.string.err_email_empty));
        }
        if(c_pass){
            etPassword.setError(getResources().getString(R.string.err_pass_empty));
        }
        if((!c_email) && (!c_pass)){
            return true;
        }else{
            return false;
        }

    }

    /**
     * Metodo encargado de mostrar una ventana emergente
     * para entrar el correo electronico en caso de haber
     * seleccionado la opcion de ¿Has olvidado la contraseña?
     *
     * @param v
     */
    public void forgotPassword(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.email_registred);
        final EditText email = new EditText(this);
        email.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setHint(R.string.et_email);
        alert.setView(email);
        alertSendEmail(alert, email);
        alert.setNegativeButton(R.string.cancel, null);
        alert.show();
    }

    /**
     * Metodo encargado de enviar el correo electronico con los pasas
     * para la reestablecer la contraseña
     *
     * @param alert
     * @param email
     */
    public void alertSendEmail(AlertDialog.Builder alert, final EditText email) {
        alert.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                if(email.getText().toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.err_notSend_email, Snackbar.LENGTH_LONG)
                            .show();
                    progressBar.setVisibility(View.GONE);
                }else{
                    mAuth.fetchProvidersForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            if (task.isSuccessful()) {
                                mAuth.sendPasswordResetEmail(email.getText().toString())
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


    /**
     * Metodo encargado de comprobar si el usuario actual
     * ya ha iniciado sesion anteriormente en el dispositivo
     * movil para asi abrir la pantalla principal de la aplicacion.
     * Si el booleano recibido por parametro es falso, significa que
     * proviene de haber dado a iniciar sesion y los datos son incorrectos.
     *
     * @param currentUser
     * @param start
     */
    private void updateUI(FirebaseUser currentUser, Boolean start) {
        if(currentUser != null){
            Controller.getInstance();
            openHome();

        }else{
            if(!start){
                Snackbar.make(findViewById(android.R.id.content), R.string.err_login, Snackbar.LENGTH_LONG)
                        .show();
            }
        }

    }

    public void openRegister(View v){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    private void showPassword() {
        Utils.showPassword(etPassword);
    }

    public void hideKeyboard(View v){
        Utils.hideKeyboard(this);
    }
}
