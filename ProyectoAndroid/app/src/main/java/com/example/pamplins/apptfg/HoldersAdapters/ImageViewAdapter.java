package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.example.pamplins.apptfg.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gustavo on 18/03/2018.
 */
public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder> {
    private Activity activity;
    private List<String> urlImagesDoubt;

    public ImageViewAdapter(Activity activity, List<String> urlImagesDoubt){
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
                if(activity.getClass().getName().contains("Answer") || activity.getClass().getName().contains("Main")) {
                    viewHolder.img.setBackgroundColor(activity.getResources().getColor(R.color.long_press));
                    viewHolder.img.setAlpha(0.5f);
                    removeImage(viewHolder.getAdapterPosition(), viewHolder.img);
                }
                return true;

            }
        });
    }

    private void removeImage(final int adapterPosition, final ImageView img) {
        new AlertDialog.Builder(activity).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                img.setAlpha(1f);
                img.setBackgroundColor(Color.WHITE);
            }
        })
        .setMessage(R.string.alert_remove_image)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                urlImagesDoubt.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, urlImagesDoubt.size());
            }})
        .setNegativeButton(R.string.not, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                img.setAlpha(1f);
                img.setBackgroundColor(Color.WHITE);
            }
        }).show();

    }


    public List<String> getUrlImagesDoubt(){
        return urlImagesDoubt;
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

