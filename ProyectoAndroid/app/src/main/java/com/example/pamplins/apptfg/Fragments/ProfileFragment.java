package com.example.pamplins.apptfg.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Course;
import com.example.pamplins.apptfg.Model.Subject;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;
import com.example.pamplins.apptfg.View.LoginActivity;
import com.example.pamplins.apptfg.View.RegisterActivity;
import com.example.pamplins.apptfg.View.SubjectActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class ProfileFragment extends DoubtsFragment {

    private Bitmap bit;
    public ProfileFragment() {
    }

    @Override
    public Query getQuery() {
        return mDatabase.child(Constants.REF_USER_DOUBTS).child(ctrl.getUid());
    }

    @Override
    public View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mRecycler = rootView.findViewById(R.id.messages_list);
        mRecycler.setNestedScrollingEnabled(false);
        //progressBar = rootView.findViewById(R.id.progressBar_h);
        TextView name = rootView.findViewById(R.id.tv_user_name_p);

        name.setText(ctrl.getUser().getUserName());
        ImageView ivUser = rootView.findViewById(R.id.img_user_profile);
        ctrl.showProfileImage(getActivity(),ctrl.getUser().getUrlProfileImage(), ivUser);
        ImageView ivLogout = rootView.findViewById(R.id.iv_signout);
        ivLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(Utils.getButtonAnimation());
                confirmExit();

            }
        });
        LinearLayout camera = rootView.findViewById(R.id.ll_camera_p);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(Utils.getButtonAnimation());
                openAlert();
            }
        });

        return rootView;

    }

    private void exit() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        ctrl.restartInstance();
        getActivity().finish();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
    }

    private void confirmExit() {
        new AlertDialog.Builder(getActivity())
            .setMessage(R.string.alert_exit)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    exit();
                }})
            .setNegativeButton(R.string.not, null).show();
    }

    /**
     * Funcion encargada de abrir el dialogo para escoger entre la
     * galeria o camara para subir una imagen
     */
    private void openAlert() {
        final CharSequence[] items = {getResources().getString(R.string.open_camera), getResources().getString(R.string.select_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getResources().getString(R.string.open_camera))) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, 0);

                } else if (items[item].equals(getResources().getString(R.string.select_gallery))){

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }

        });
        builder.show();
    }
    /**
     * Metodo encargado de obtener la imagen que el usuario
     * ha seleccionado, ya sea desde camara o desde storage
     *
     * @param requestCode
     * @param resultCode
     * @param imageReturnedIntent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            if(requestCode == 0){
                bit = (Bitmap) imageReturnedIntent.getExtras().get("data");
                ImageView img = getActivity().findViewById(R.id.img_user_profile);
                img.setDrawingCacheEnabled(true);
                img.buildDrawingCache();
                img.setImageBitmap(bit);
            }else{
                try {
                    Uri uri = imageReturnedIntent.getData();
                    InputStream imageStream =  getActivity().getContentResolver().openInputStream(uri);
                    bit = BitmapFactory.decodeStream(imageStream);
                    ImageView img = getActivity().findViewById(R.id.img_user_profile);
                    img.setDrawingCacheEnabled(true);
                    img.buildDrawingCache();
                    img.setImageBitmap(bit);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            ctrl.uploadImageProfile(ctrl.getUid(), "", "", bit, "image_profile.jpg", 1);

        }
    }

}
