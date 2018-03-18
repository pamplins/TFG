package com.example.pamplins.apptfg.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.ImageViewAdapter;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.User;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;
import com.example.pamplins.apptfg.View.Register;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private ArrayList<String> donwloadurlsImages;

    public NewDoubtFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlsImages = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ctrl = Controller.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        View view = inflater.inflate(R.layout.fragment_new_doubt,
                container, false);

        etTitle = view.findViewById(R.id.et_title_new_post);
        etDescription = view.findViewById(R.id.et_description_new_post);
        Button btnUpload = view.findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });

        Button btnNewDoubt = view.findViewById(R.id.btn_newDoubt);
        btnNewDoubt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                submitPost();
            }
        });

        return view;

    }

    private void selectImage() {

        if(ctrl.verifyPermissions(getActivity())) {
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
                    mRecycler_items = getActivity().findViewById(R.id.recycle_items_doubt_nd);
                    mRecycler_items.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    mRecycler_items.setLayoutManager(linearLayoutManager);

                    ImageViewAdapter imageViewAdapter = new ImageViewAdapter(getActivity(), urlsImages);
                    mRecycler_items.setAdapter(imageViewAdapter);

                }
            }

        }

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
        if(!urlsImages.isEmpty()){
            uploadImageProfile(userId, "doubt_images/", "image-doubt", title, body, user);
        }else{
            String key = FirebaseDatabase.getInstance().getReference().child(Constants.REF_DOUBTS).push().getKey();
            String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Doubt doubt = new Doubt(userId, title, body, date, user, urlsImages);
            Map<String, Object> postValues = doubt.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/doubts/" + key, postValues);
            childUpdates.put("/user_doubts/" + doubt.getUid() + "/" + key, postValues);
            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
        }
    }

    public void uploadImageProfile(final String uid, String folder, final String path, final String title, final String body, final User user) {
        donwloadurlsImages = new ArrayList<>();
        for(int i = 0; i < urlsImages.size(); i++){
            String ref = folder + uid + "/" + path+"_"+i+".jpg"; // string de la ruta a la que ira
            StorageReference fileToUpload;
            fileToUpload = FirebaseStorage.getInstance().getReference().child(ref);

            final int finalI = i;
            fileToUpload.putFile(Uri.parse(urlsImages.get(i))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    donwloadurlsImages.add(downloadUrl.toString());
                    if(finalI == urlsImages.size()-1){
                        String key = FirebaseDatabase.getInstance().getReference().child(Constants.REF_DOUBTS).push().getKey();
                        String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        Doubt doubt = new Doubt(uid, title, body, date, user, donwloadurlsImages);
                        Map<String, Object> postValues = doubt.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/doubts/" + key, postValues);
                        childUpdates.put("/user_doubts/" + doubt.getUid() + "/" + key, postValues);
                        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                    }
                }
            });
        }
    }

}
