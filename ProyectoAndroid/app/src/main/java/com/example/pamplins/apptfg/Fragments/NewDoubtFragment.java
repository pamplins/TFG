package com.example.pamplins.apptfg.Fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
import com.example.pamplins.apptfg.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by Gustavo on 17/02/2018.
 */


public class NewDoubtFragment extends Fragment {

    private DatabaseReference mDatabase;

    private EditText etTitle;
    private EditText etDescription;
    private Controller ctrl;

    public NewDoubtFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initElements() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ctrl = Controller.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        View view = inflater.inflate(R.layout.fragment_new_doubt,
                container, false);

        etTitle = view.findViewById(R.id.et_title_new_post);
        etDescription = view.findViewById(R.id.et_description_new_post);

        Button button = view.findViewById(R.id.btn_newDoubt);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                submitPost();

            }
        });
        return view;

    }

    private void submitPost() {
        final String title = etTitle.getText().toString();
        final String description = etDescription.getText().toString();
         if (checkInputs(title, description)){
            mDatabase.child("users").child(ctrl.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                    writeNewDoubt(ctrl.getUid(), user.getUserName(), title, description);
                                    ctrl.hideKeyboard(getActivity());
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Tu duda se ha posteado correctamente" , Snackbar.LENGTH_LONG)
                                            .show();
                                } else {
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.err_post , Snackbar.LENGTH_LONG)
                                            .show();
                                }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }


    }

    private boolean checkInputs(String title, String description){
        if(title.isEmpty()){
            etTitle.setError("Entra titulo");
        }
        if(description.isEmpty()){
            etDescription.setError("Entra descripcion");
        }

        if((!title.isEmpty()) && (!description.isEmpty())){
            return true;
        }
        return false;

    }
    private void writeNewDoubt(String userId, String username, String title, String body) {
        String key = mDatabase.child("doubts").push().getKey();
        String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Doubt doubt = new Doubt(userId, username, title, body, date);
        Map<String, Object> postValues = doubt.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/doubts/" + key, postValues);
        childUpdates.put("/user_doubts/" + userId + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }


}
