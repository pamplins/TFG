package com.example.pamplins.apptfg;

/**
 * Created by Gustavo on 21/02/2018.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Fragments.HomeFragment;
import com.example.pamplins.apptfg.Model.Doubt;

public class DoubtViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    //public ImageView starView;
    //public TextView numStarsView;
    public TextView bodyView;
    public TextView date;
    public ImageView img;

    public DoubtViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        //starView = itemView.findViewById(R.id.likes);
        //numStarsView = itemView.findViewById(R.id.post_num_likes);
        bodyView = itemView.findViewById(R.id.post_description);
        date = itemView.findViewById(R.id.tv_date);
        img = itemView.findViewById(R.id.post_author_photo);
    }

    public void bindToPost(Doubt doubt, Activity activity, Controller ctrl, String uid, View.OnClickListener starClickListener) {
        titleView.setText(doubt.getTitle());
        authorView.setText(doubt.getAuthor());
        //numStarsView.setText(String.valueOf(doubt.getLikesCount()));
        if (doubt.getDescription().trim().length() > 100) {
            bodyView.setText(doubt.getDescription().substring(0, 100) + "...");
        } else {
            bodyView.setText(doubt.getDescription());
        }
        date.setText(doubt.getDate());

        ctrl.drawImage(activity, img, uid);


    }
       /*
        Bitmap bit = loadBitmapFromView(img);
        // TODO ver tambien aqui lo de ponerr eso
        img.setImageBitmap(Utils.getCircularBitmap(bit));
        // Load the image using Glide
        Glide.with(activity.getBaseContext())
                .load("https://firebasestorage.googleapis.com/v0/b/app-tfg-gati1304.appspot.com/o/user_images%2FCxfW0ucTDFgOvGh1SPCfMGUWm5u2%2Fimage_profile.jpg?alt=media&token=05095ccf-0488-4396-8a2b-46d10b6eac42")
                .into(img);
        //starView.setOnClickListener(starClickListener);
    }

    private static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }*/
}
