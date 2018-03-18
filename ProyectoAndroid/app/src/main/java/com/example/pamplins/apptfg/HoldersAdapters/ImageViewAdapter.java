package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;

/**
 * Created by Gustavo on 18/03/2018.
 */
public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder> {
    private Activity activity;
    private Doubt currentDoubt;

    public ImageViewAdapter(Activity activity, Doubt currentDoubt){
        this.activity = activity;
        this.currentDoubt = currentDoubt;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_carousel
                , viewGroup, false);
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ImageViewHolder viewHolder, int position) {
        final ImagePopup imagePopup = new ImagePopup(activity);
        imagePopup.initiatePopupWithGlide(currentDoubt.getUrlImage());
        imagePopup.setFullScreen(true);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePopup.viewPopup();
            }
        });
        try{
            if(!currentDoubt.getUrlImage().equals("")){
                Glide.with(activity)
                        .load(currentDoubt.getUrlImage())
                        .into(viewHolder.img);
            }else{

            }
        }catch (Exception ex){

        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ImageViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_imageDoubtDetails);
        }

    }
}

