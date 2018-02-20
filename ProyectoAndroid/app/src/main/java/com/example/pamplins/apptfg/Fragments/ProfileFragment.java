package com.example.pamplins.apptfg.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.View.Login;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_profile,
                container, false);
        Button button = view.findViewById(R.id.btn_signout);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                firebaseAuth.signOut();
                getActivity().finish();
                Intent i = new Intent(getActivity(), Login.class);
                startActivity(i);


            }
        });
        return view;
    }

}
