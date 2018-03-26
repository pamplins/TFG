package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                .into(viewHolder.img);
        final ImagePopup imagePopup = new ImagePopup(activity);
        imagePopup.initiatePopupWithGlide(url);
        imagePopup.setFullScreen(true);
        imagePopup.setImageOnClickClose(true);
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePopup.viewPopup();
            }
        });

        viewHolder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(activity.findViewById(android.R.id.content), activity.getClass().getName(), Snackbar.LENGTH_LONG)
                        .show();
                if(activity.getClass().getName().contains("MainActivity")) {
                    removeItem(viewHolder.getAdapterPosition());
                }
                return true;

            }
        });
    }

    private void removeItem(int adapterPosition) {
        urlImagesDoubt.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyItemRangeChanged(adapterPosition, urlImagesDoubt.size());
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

