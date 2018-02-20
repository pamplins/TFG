package com.example.pamplins.apptfg.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Post;
import com.example.pamplins.apptfg.Model.User;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.View.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Gustavo on 17/02/2018.
 */


public class NewDoubtFragment extends Fragment {


    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private EditText etTitle;
    private EditText etDescription;
    private FloatingActionButton mSubmitButton;
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
       // return inflater.inflate(R.layout.fragment_new_doubt, container, false);

        //mSubmitButton = getView().findViewById(R.id.fab_submit_post);
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
        final String body = etDescription.getText().toString();

        // habra comprobacion de minimo y maximo para cada uno

        //final String aux_uid = ctrl.getCurrentUser().getUid();


       final String aux_uid = ctrl.getCurrentUser().getUid();
        //TODO cambiar la key de la base de datos por UID seguramente
        // ya que comprobacion de user se hace solo una vez en inicar sesion. y estar cogiendo el UID se hace mas veces.
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if(user != null){
                        if (user.getUid().equals(aux_uid)) {
                            writeNewPost(user.getUid(), user.getUserName(), title, body);
                            break;
                        }
                    }else{
                        Toast.makeText(getActivity(),
                                "NO se ha encontrado el usuario",
                                Toast.LENGTH_SHORT).show();
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabase.child("users").addListenerForSingleValueEvent(eventListener);

    }


    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + username + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }


}
