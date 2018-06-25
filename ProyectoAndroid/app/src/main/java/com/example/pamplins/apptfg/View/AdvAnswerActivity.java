package com.example.pamplins.apptfg.View;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Gustavo on 06/06/2018.
 */

public class AdvAnswerActivity extends AppCompatActivity {

    private EditText etAnswer;
    private Controller ctrl;
    private RecyclerView mRecycler_items;
    private List<String> urlsImages;
    private HashMap<String, Bitmap> bitImages;
    private ProgressBar progressBar;
    private TextView tvUpload;
    private ImageView tvNewAdvRes;
    private Doubt currentDoubt;
    private String doubtKey;
    private String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_response);
        initToolbar();
        currentDoubt = (Doubt) getIntent().getSerializableExtra("currentDoubt");
        doubtKey = (String) getIntent().getSerializableExtra("doubtKey");
        answer = "";
        if(getIntent().getSerializableExtra("etAnswer") != null){
            answer = (String) getIntent().getSerializableExtra("etAnswer");
        }
        if (currentDoubt == null || doubtKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        initElements();

    }

    private void initElements() {
        ctrl = Controller.getInstance();
        progressBar = findViewById(R.id.progressBar_naa);
        etAnswer = findViewById(R.id.et_description_new_answer);
        if(!answer.isEmpty()){
            etAnswer.setText(answer);
            etAnswer.setSelection(answer.length());
        }
        tvUpload = findViewById(R.id.tv_upload_adv_res);
        tvUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(Utils.getButtonAnimation());
                selectImage();
            }
        });
        tvNewAdvRes = findViewById(R.id.iv_new_adv_res);
        tvNewAdvRes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(Utils.getButtonAnimation());
                checkAnswer();
            }
        });
        urlsImages = new ArrayList<>();
        bitImages = new HashMap<>();
    }

    private void initToolbar(){
        Toolbar myToolbar = findViewById(R.id.tool_subject);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_response);
        myToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorButton), PorterDuff.Mode.SRC_ATOP);
    }

    private void checkAnswer() {
        tvNewAdvRes.setEnabled(false);
        final String answer = etAnswer.getText().toString();
        if(Utils.isNetworkAvailable(this)) {
            if (checkInputs(answer)) {
                sendAnswer();

            }else{
                tvNewAdvRes.setEnabled(true);
            }
        }else{
            tvNewAdvRes.setEnabled(true);
            Snackbar.make(findViewById(android.R.id.content), R.string.err_conex, Snackbar.LENGTH_LONG)
                    .show();
        }

    }

    /**
     * Metodo encargado de enviar a controlador los datos de la respuesta avanzada para que este la suba al servidor
     */
    private void sendAnswer() {
        preWritAnswer();
        final String answerText = etAnswer.getText().toString();
        if (answerText.trim().isEmpty()) {
            etAnswer.setError(getResources().getString(R.string.empty_answer));
        }else{
            if(bitImages.isEmpty()){
                ctrl.writeAnswerDB(currentDoubt, doubtKey, answerText, null, AdvAnswerActivity.this, etAnswer);
            }else{
                ctrl.uploadImages(bitImages, currentDoubt, doubtKey, answerText, AdvAnswerActivity.this, etAnswer);
            }
        }
    }

    /**
     * Funcion encargada de deshabilitar la pantalla una vez el usuario ha presionado el boton de subir la duda
     * para que asi no hayan errores al subirse
     */
    private void preWritAnswer() {
        Utils.hideKeyboard(this);
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, // bloquea la pantalla hasta que la duda se suba
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        tvNewAdvRes.setEnabled(true);

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

    /**
     * Metodo encargado de comprobar que imagenes e han eliminado en las urls del adapter de imagen
     * para asi tambien eliminarlas de la hash de bits
     */
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
     *
     * */
    private void openAlert() {
        final CharSequence[] items = {getResources().getString(R.string.open_camera), getResources().getString(R.string.select_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                Uri fileUri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), null, null));
                urlsImages.add(fileUri.toString());
                bitImages.put(fileUri.toString(), MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            createRecycleView();
        }
        else if(requestCode == 1 && resultCode == RESULT_OK){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(urlsImages.size() < 9) {
                    if (data.getClipData() != null) {
                        multipleSelectionImages(data);
                    } else if (data.getData() != null) {
                        selectionImage(data);
                    }
                }else{
                    Snackbar.make(findViewById(android.R.id.content), R.string.limit_max_images, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

    private void selectionImage(Intent data) {
        Uri fileUri = data.getData();
        urlsImages.add(fileUri.toString());
        try {
            bitImages.put(fileUri.toString(), MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        createRecycleView();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void multipleSelectionImages(Intent data) {
        int totalItemsSelected = data.getClipData().getItemCount();
        if (totalItemsSelected < 9 && (urlsImages.size()-1+totalItemsSelected) < 9) {
            Uri fileUri;
            for (int i = 0; i < totalItemsSelected; i++) {
                fileUri = data.getClipData().getItemAt(i).getUri();
                urlsImages.add(fileUri.toString());
                try {
                    bitImages.put(fileUri.toString(), MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            createRecycleView();
        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.err_max_images, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void confirmExit(){
        if(!etAnswer.getText().toString().isEmpty()  || !bitImages.isEmpty()){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.exit_with_adv_answer)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }})
                    .setNegativeButton(R.string.not, null).show();
        }else{
            finish();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for(String key: bitImages.keySet()) {
            bitImages.get(key).recycle();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        confirmExit();
        return true;
    }


    @Override
    public void onBackPressed() {
        confirmExit();
    }
}
