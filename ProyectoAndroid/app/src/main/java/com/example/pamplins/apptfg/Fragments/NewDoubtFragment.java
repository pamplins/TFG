package com.example.pamplins.apptfg.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
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
import java.util.Date;
import java.util.HashMap;
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
    private ImageView imImageDoubte;
    private Bitmap bit;
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
        imImageDoubte = view.findViewById(R.id.iv_imageDoubt);
        bit = null;
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
        final CharSequence[] items = {"Hacer foto", "Seleccionar de galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir imagen");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Hacer foto")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, 0);

                } else if (items[item].equals("Seleccionar de galeria")) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }

        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            if(requestCode == 0){
                bit = (Bitmap) imageReturnedIntent.getExtras().get("data");
                imImageDoubte.setDrawingCacheEnabled(true);
                imImageDoubte.buildDrawingCache();
                imImageDoubte.setImageBitmap(Utils.getCircularBitmap(bit));
            }else{
                try {
                    Uri uri = imageReturnedIntent.getData();
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(uri);
                    bit = BitmapFactory.decodeStream(imageStream);
                    imImageDoubte.setDrawingCacheEnabled(true);
                    imImageDoubte.buildDrawingCache();
                    imImageDoubte.setImageBitmap(Utils.getCircularBitmap(bit));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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
        if(bit != null){
            uploadImageProfile(userId, bit, "doubt_images/", "image-doubt.jpg", title, body, user);
        }else{

            String key = FirebaseDatabase.getInstance().getReference().child(Constants.REF_DOUBTS).push().getKey();
            String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Doubt doubt = new Doubt(userId, title, body, date, user, "");
            Map<String, Object> postValues = doubt.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/doubts/" + key, postValues);
            childUpdates.put("/user_doubts/" + doubt.getUid() + "/" + key, postValues);
            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
        }
    }

    public static void uploadImageProfile(final String uid, Bitmap bit, String folder, final String path, final String title, final String body, final User user) {
        String ref = folder + uid + "/" + path; // string de la ruta a la que ira
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference().child(ref);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        mStorageRef.putBytes(data);
        UploadTask uploadTask = mStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //TODO el usuario no se ha podido registrar porque no se ha podido subir la foto
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                String key = FirebaseDatabase.getInstance().getReference().child(Constants.REF_DOUBTS).push().getKey();
                String date =  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Doubt doubt = new Doubt(uid, title, body, date, user, downloadUrl.toString());
                Map<String, Object> postValues = doubt.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/doubts/" + key, postValues);
                childUpdates.put("/user_doubts/" + doubt.getUid() + "/" + key, postValues);
                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
            }
        });
    }




}
