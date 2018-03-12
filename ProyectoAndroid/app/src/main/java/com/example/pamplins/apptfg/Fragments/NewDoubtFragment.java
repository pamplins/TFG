package com.example.pamplins.apptfg.Fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
import com.example.pamplins.apptfg.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            mDatabase.child(Constants.REF_USERS).child(ctrl.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
                                   //writeNewDoubt(ctrl.getUid(), user.getUserName(), title, description);
                                    writeNewDoubt(ctrl.getUid(), title, description, user);
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
    private void writeNewDoubt(String userId, String title, String body, User user) {
        String key = mDatabase.child(Constants.REF_DOUBTS).push().getKey();
        String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Doubt doubt = new Doubt(userId, title, body, date, user);
        Map<String, Object> postValues = doubt.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/doubts/" + key, postValues);
        childUpdates.put("/user_doubts/" + doubt.getUid() + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }


}
