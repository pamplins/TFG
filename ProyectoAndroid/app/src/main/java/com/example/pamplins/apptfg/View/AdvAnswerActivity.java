package com.example.pamplins.apptfg.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.ImageViewAdapter;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gustavo on 06/06/2018.
 */

public class AdvAnswerActivity extends AppCompatActivity {

    private EditText etAnswer;
    private Controller ctrl;

    private RecyclerView mRecycler_items;
    private List<String> urlsImages;
    private List<Bitmap> bitImages;

    private ProgressBar progressBar;
    private TextView tvUpload;
    private ImageView tvNewAdvRes;
    private Doubt currentDoubt;
    private String doubtKey;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_response);
        initToolbar();
        currentDoubt = (Doubt) getIntent().getSerializableExtra("currentDoubt");
        doubtKey = (String) getIntent().getSerializableExtra("doubtKey");
        if (currentDoubt == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        ctrl = Controller.getInstance();
        progressBar = findViewById(R.id.progressBar_naa);
        etAnswer = findViewById(R.id.et_description_new_answer);

        tvUpload = findViewById(R.id.tv_upload_adv_res);

        tvUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });

        tvNewAdvRes = findViewById(R.id.iv_new_adv_res);

        tvNewAdvRes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendDoubt();
            }
        });
        urlsImages = new ArrayList<>();
        bitImages = new ArrayList<>();
    }
    /**
     * Funcion encargada de deshabilitar la pantalla una vez el usuario ha presionado el boton de subir la duda
     * para que asi no hayan errores al subirse
     */
    private void preWriteDoubt() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Utils.hideKeyboard(this);
    }
    private void initToolbar(){
        Toolbar myToolbar = findViewById(R.id.tool_subject);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_response);
        myToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorButton), PorterDuff.Mode.SRC_ATOP);
    }

    private void sendDoubt() {
        final String answer = etAnswer.getText().toString();
        if(Utils.isNetworkAvailable(this)) {
            if (checkInputs(answer)) {
                //preWriteDoubt();
                final String answerText = etAnswer.getText().toString();
                if (answerText.trim().isEmpty()) {
                    etAnswer.setError(getResources().getString(R.string.empty_answer));
                }else{
                    if(bitImages.isEmpty()){
                        ctrl.writeAnswerDB(currentDoubt, doubtKey, answerText, tvNewAdvRes, null, AdvAnswerActivity.this);
                    }else{
                        ctrl.uploadImages(bitImages, currentDoubt, doubtKey, answerText, tvNewAdvRes, AdvAnswerActivity.this);
                    }
                    Utils.hideKeyboard(AdvAnswerActivity.this);
                }

                if (!urlsImages.isEmpty()) {
                    urlsImages.clear();
                    mRecycler_items.getAdapter().notifyDataSetChanged();

                }
            }
        }else{
            Snackbar.make(findViewById(android.R.id.content), R.string.err_conex, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private boolean checkInputs(String answer){
        boolean c_answer = answer.trim().length() < 10;

        if(c_answer){
            etAnswer.setError(getResources().getString(R.string.err_len_et));
        }
        if(answer.trim().isEmpty()){
            etAnswer.setError(getResources().getString(R.string.empty_answer));
        }

        if(!(c_answer)){
            return true;
        }
        return false;

    }

    /**
     * Funcion encargada de craear el recycleView que es donde iran las imagenes seleccionadas
     */
    private void createRecycleView() {
        mRecycler_items = this.findViewById(R.id.recycle_items_answer);
        mRecycler_items.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecycler_items.setLayoutManager(linearLayoutManager);
        ImageViewAdapter imageViewAdapter = new ImageViewAdapter(this, urlsImages);
        mRecycler_items.setAdapter(imageViewAdapter);
    }

    private void selectImage() {
        if(Utils.verifyPermissions(this)) {
            openAlert();
        }else{
            Snackbar.make(findViewById(android.R.id.content), R.string.permiss, Snackbar.LENGTH_LONG)
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
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bitImages.add(bitmap);

                    }
                    createRecycleView();
                }else if(data.getData() != null){
                    Uri fileUri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
