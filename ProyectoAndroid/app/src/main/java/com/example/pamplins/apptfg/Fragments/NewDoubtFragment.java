package com.example.pamplins.apptfg.Fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import com.example.pamplins.apptfg.View.DoubtDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Map<String, Bitmap> bitImages;

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
        setRetainInstance(true);

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
                v.startAnimation(Utils.getButtonAnimation());
                selectImage();
            }
        });

        tvNewDoubt = view.findViewById(R.id.tv_newDoubt);

        tvNewDoubt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(Utils.getButtonAnimation());
                sendDoubt();
            }
        });
        urlsImages = new ArrayList<>();
        bitImages = new HashMap<>();

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
     */
   /* private void openAlert() {

    }*/

    /**
     * Funcion encargada de abrir el dialogo para escoger entre la galeria o camara para subir una imagen
     *
     * */
    private void openAlert() {
        final CharSequence[] items = {getResources().getString(R.string.open_camera), getResources().getString(R.string.select_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getResources().getString(R.string.open_camera))) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/tmp")));
                    startActivityForResult(takePictureIntent, 0);

                } else if (items[item].equals(getResources().getString(R.string.select_gallery))){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
                }
            }

        });
        builder.show();
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
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            File file = new File("/sdcard/tmp");
            try {
                Uri fileUri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), null, null));
                urlsImages.add(fileUri.toString());
                bitImages.put(fileUri.toString(), MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            createRecycleView();
        }else if(requestCode == 1 && resultCode == RESULT_OK){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(urlsImages.size() < 9) {
                    if (data.getClipData() != null) {
                        int totalItemsSelected = data.getClipData().getItemCount();
                        if (totalItemsSelected < 9 && (urlsImages.size()-1+totalItemsSelected) < 9) {
                            Uri fileUri;
                            for (int i = 0; i < totalItemsSelected; i++) {
                                fileUri = data.getClipData().getItemAt(i).getUri();
                                urlsImages.add(fileUri.toString());
                                try {
                                    bitImages.put(fileUri.toString(), MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            createRecycleView();
                        } else {
                            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.err_max_images, Snackbar.LENGTH_LONG)
                                    .show();
                        }

                    } else if (data.getData() != null) {
                        Uri fileUri = data.getData();
                        urlsImages.add(fileUri.toString());
                        try {
                            bitImages.put(fileUri.toString(), MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        createRecycleView();
                    }
                }else{
                    Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.limit_max_images, Snackbar.LENGTH_LONG)
                            .show();
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
        imageViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if(urlsImages.isEmpty()){
                    bitImages.clear();
                }else{
                    checkImagesDeleted();
                }
            }
        });
    }

    private void checkImagesDeleted() {
        Iterator<Map.Entry<String,Bitmap>> iter = bitImages.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,Bitmap> entry = iter.next();
            if(!urlsImages.contains(entry.getKey())) {
                bitImages.get(entry.getKey()).recycle();
                iter.remove();
            }
        }
    }

    private void sendDoubt() {
        tvNewDoubt.setEnabled(false);
        final String title = etTitle.getText().toString();
        final String description = etDescription.getText().toString();
        final String subject = textView.getText().toString();

        if(Utils.isNetworkAvailable(getActivity())) {
            if (checkInputs(title, description, subject)) {
                preWriteDoubt();
                ctrl.writeDoubtDB(title, description, bitImages, getActivity(), etTitle, etDescription, textView, progressBar, tvUpload, tvNewDoubt, subject, urlsImages, mRecycler_items);
            }else{
                tvNewDoubt.setEnabled(true);
            }
        }else{
            tvNewDoubt.setEnabled(true);
            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.err_conex, Snackbar.LENGTH_LONG)
                    .show();
        }

    }


    /**
     * Funcion encargada de deshabilitar la pantalla una vez el usuario ha presionado el boton de subir la duda
     * para que asi no hayan errores al subirse
     */
    private void preWriteDoubt() {
        Utils.hideKeyboard(getActivity());
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, // bloquea la pantalla hasta que la duda se suba
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        tvNewDoubt.setEnabled(true);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        for(String key: bitImages.keySet()) {
            bitImages.get(key).recycle();
        }
    }
    private void setBtnDoubt(boolean enabled) {
        tvNewDoubt.setEnabled(enabled);
    }

}
