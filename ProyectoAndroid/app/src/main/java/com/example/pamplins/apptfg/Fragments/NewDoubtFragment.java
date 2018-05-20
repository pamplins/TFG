package com.example.pamplins.apptfg.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.ImageViewAdapter;
import com.example.pamplins.apptfg.Model.Subject;
import com.example.pamplins.apptfg.Model.User;
import com.example.pamplins.apptfg.R;

import com.example.pamplins.apptfg.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Gustavo on 17/02/2018.
 */


public class NewDoubtFragment extends Fragment {

    private DatabaseReference mDatabase;

    private EditText etTitle;
    private EditText etDescription;
    private Controller ctrl;

    private RecyclerView mRecycler_items;
    private ArrayList<String> urlsImages;
    private TextView tvUpload;
    private ImageView tvNewDoubt;

    private ProgressBar progressBar;
    private List<String> subjects;
    AutoCompleteTextView textView;

    public NewDoubtFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ctrl = Controller.getInstance();
        autocomplete();
        super.onCreate(savedInstanceState);
    }

    private void autocomplete(){
        ctrl.getSubjectsRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjects = new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String subject = snapshot.getKey();
                    subjects.add(subject);
                }

                try{
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line, subjects);
                    textView = getActivity().findViewById(R.id.et_autocomplete);
                    textView.setAdapter(adapter);
                }catch (Exception e){

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        View view = inflater.inflate(R.layout.fragment_new_doubt,
                container, false);

        progressBar = view.findViewById(R.id.progressBar_nd);
        etTitle = view.findViewById(R.id.et_title_new_post);
        etDescription = view.findViewById(R.id.et_description_new_post);
        tvUpload = view.findViewById(R.id.tv_upload);

        tvUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });

        tvNewDoubt = view.findViewById(R.id.tv_newDoubt);

        tvNewDoubt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendDoubt();
            }
        });
        urlsImages = new ArrayList<>();

        return view;
    }

    private void selectImage() {
        if(Utils.verifyPermissions(getActivity())) {
            openAlert();
        }else{
            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.permiss, Snackbar.LENGTH_LONG)
                    .show();
        }
    }
    /**
     * Funcion encargada de abrir el dialogo para escoger entre la galeria o camara para subir una imagen
     */
    private void openAlert() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(data.getClipData() != null){
                    int totalItemsSelected = data.getClipData().getItemCount();
                    Uri fileUri;
                    for(int i = 0; i < totalItemsSelected; i++){
                        fileUri = data.getClipData().getItemAt(i).getUri();
                        urlsImages.add(fileUri.toString());
                    }
                    createRecycleView();
                }else if(data.getData() != null){
                    Uri fileUri = data.getData();
                    urlsImages.add(fileUri.toString());
                    createRecycleView();
                }

            }
        }
    }

    private void createRecycleView() {
        mRecycler_items = getActivity().findViewById(R.id.recycle_items_doubt_nd);
        mRecycler_items.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecycler_items.setLayoutManager(linearLayoutManager);
        ImageViewAdapter imageViewAdapter = new ImageViewAdapter(getActivity(), urlsImages);
        mRecycler_items.setAdapter(imageViewAdapter);
    }

    private void sendDoubt() {
        final String title = etTitle.getText().toString();
        final String description = etDescription.getText().toString();
        final String subject = textView.getText().toString();
         if (checkInputs(title, description, subject)){
             preWriteDoubt();
             ctrl.writeDoubtDB(title, description, urlsImages, new ArrayList<String>(), mRecycler_items, getActivity(), etTitle, etDescription, textView, progressBar, tvUpload, tvNewDoubt, subject);
        }
    }



    private void preWriteDoubt() {
        progressBar.setVisibility(View.VISIBLE);
        setBtnDoubt(false); // evitar multiples creaciones de dudas
        etTitle.setEnabled(false);
        etDescription.setEnabled(false);
        textView.setEnabled(false);
        tvUpload.setEnabled(false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, // bloquea la pantalla hasta que la duda se suba
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Utils.hideKeyboard(getActivity());
    }

    private boolean checkInputs(String title, String description, String subject){
        boolean c_title = title.trim().length() < 7;
        boolean c_descp = description.trim().length() < 10;
        boolean c_subject = subject.trim().length() < 1;

        if(c_title){
            etTitle.setError("Entra titulo");
        }
        if(c_descp){
            etDescription.setError("Entra descripcion");
        }
        if(c_subject){
            textView.setError("Entra una asignatura");
        }else{
            if(!subjects.contains(subject)){
                textView.setError("Asignatura incorrecta");
                c_subject = true;
            }
        }


        if(!(c_title) && !(c_descp) && !(c_subject)){
            return true;
        }
        return false;

    }

    private void setBtnDoubt(boolean enabled) {
        tvNewDoubt.setEnabled(enabled);
    }

}
