package com.example.pamplins.apptfg.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.ImageViewAdapter;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Gustavo on 17/02/2018.
 */


public class NewDoubtFragment extends Fragment {

    private EditText etTitle;
    private EditText etDescription;
    private Controller ctrl;

    private RecyclerView mRecycler_items;
    private List<String> urlsImages;
    private List<Bitmap> bitImages;

    private TextView tvUpload;
    private ImageView tvNewDoubt;

    private ProgressBar progressBar;
    private List<String> subjects;
    private AutoCompleteTextView textView;

    public NewDoubtFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctrl = Controller.getInstance();
        autocomplete();
    }

    /**
     * Metodo encargado de obtener todas las asignaturas de la base de datos
     * y mostrar por pantalla las asignaturas que contienen en el nombre
     * lo que el usuario ha escrito por teclado
     */
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
                    textView = getActivity().findViewById(R.id.et_autocomplete);
                    textView.setThreshold(1);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line, subjects);
                    textView.setAdapter(adapter);
                    if(!textView.getText().toString().isEmpty()){
                        textView.showDropDown();
                    }
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
        bitImages = new ArrayList<>();

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

    /**
     *  Metodo encargado de obtener la imagen que el usuario
     * ha seleccionado, ya sea desde camara o desde storage
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
             Bitmap bitmap = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(data.getClipData() != null){
                    int totalItemsSelected = data.getClipData().getItemCount();
                    Uri fileUri;
                    for(int i = 0; i < totalItemsSelected; i++){
                        fileUri = data.getClipData().getItemAt(i).getUri();
                        urlsImages.add(fileUri.toString());
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bitImages.add(bitmap);

                    }
                    createRecycleView();
                }else if(data.getData() != null){
                    Uri fileUri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    urlsImages.add(fileUri.toString());
                    bitImages.add(bitmap);
                    createRecycleView();
                }

            }
        }
    }

    /**
     * Funcion encargada de craear el recycleView que es donde iran las imagenes seleccionadas
     */
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
        if(Utils.isNetworkAvailable(getActivity())) {
            if (checkInputs(title, description, subject)) {
                preWriteDoubt();
                ctrl.writeDoubtDB(title, description, bitImages, getActivity(), etTitle, etDescription, textView, progressBar, tvUpload, tvNewDoubt, subject);
                if (!urlsImages.isEmpty()) {
                    urlsImages.clear();
                    mRecycler_items.getAdapter().notifyDataSetChanged();

                }
            }
        }else{
            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.err_conex, Snackbar.LENGTH_LONG)
                    .show();
        }
    }


    /**
     * Funcion encargada de deshabilitar la pantalla una vez el usuario ha presionado el boton de subir la duda
     * para que asi no hayan errores al subirse
     */
    private void preWriteDoubt() {
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, // bloquea la pantalla hasta que la duda se suba
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Utils.hideKeyboard(getActivity());
    }

    private boolean checkInputs(String title, String description, String subject){
        boolean c_title = title.trim().length() < 7;
        boolean c_descp = description.trim().length() < 10;
        boolean c_subject = subject.trim().length() < 1;

        if(c_title){
            etTitle.setError(getResources().getString(R.string.err_len_et));
        }
        if(title.trim().isEmpty()){
            etTitle.setError(getResources().getString(R.string.err_title_empty));
        }
        if(c_descp){
            etDescription.setError(getResources().getString(R.string.err_len_et));
        }
        if(description.trim().isEmpty()){
            etDescription.setError(getResources().getString(R.string.err_description_empty));
        }

        if(c_subject){
            textView.setError(getResources().getString(R.string.err_subject_empty));
        }else{
            if(!subjects.contains(subject)){
                textView.setError(getResources().getString(R.string.err_subject));
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
