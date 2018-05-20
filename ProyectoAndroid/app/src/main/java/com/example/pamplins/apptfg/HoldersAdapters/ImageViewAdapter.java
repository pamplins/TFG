package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.pamplins.apptfg.R;

import java.util.ArrayList;

/**
 * Created by Gustavo on 18/03/2018.
 */
public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder> {
    private Activity activity;
    private ArrayList<String> urlImagesDoubt;

    public ImageViewAdapter(Activity activity, ArrayList<String> urlImagesDoubt){
        this.activity = activity;
        this.urlImagesDoubt = urlImagesDoubt;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_carousel
                , viewGroup, false);
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ImageViewHolder viewHolder, int position) {
        String url = urlImagesDoubt.get(position);
        Glide.with(activity)
                .load(url)
                .thumbnail(0.1f)
                .into(viewHolder.img);
        final ImagePopup imagePopup = new ImagePopup(activity);
        imagePopup.initiatePopupWithGlide(url);
        imagePopup.setFullScreen(true);
        imagePopup.setImageOnClickClose(true);
        /*
        PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(Your_Image_View);
        pAttacher.update();
        Add below line in build.gradle:

        compile 'com.commit451:PhotoView:1.2.4
         */
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                imagePopup.viewPopup();

            }
        });

        viewHolder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(activity.getClass().getName().contains("MainActivity")) {
                    removeItem(viewHolder.getAdapterPosition());
                }
                return true;

            }
        });
    }

    private void removeItem(final int adapterPosition) {
        new AlertDialog.Builder(activity)
        .setMessage("¿Realmente quieres eliminar esta imagen?")
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                urlImagesDoubt.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, urlImagesDoubt.size());
            }})
        .setNegativeButton("No", null).show();

    }


    @Override
    public int getItemCount() {
        return urlImagesDoubt.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        public ImageViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_imageDoubtDetails);
        }
    }
}

