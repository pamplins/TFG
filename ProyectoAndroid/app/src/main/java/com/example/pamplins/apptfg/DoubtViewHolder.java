package com.example.pamplins.apptfg;

/**
 * Created by Gustavo on 21/02/2018.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pamplins.apptfg.Model.Doubt;

public class DoubtViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;

    public DoubtViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        starView = itemView.findViewById(R.id.likes);
        numStarsView = itemView.findViewById(R.id.post_num_likes);
        bodyView = itemView.findViewById(R.id.post_description);
    }

    public void bindToPost(Doubt doubt, View.OnClickListener starClickListener) {
        titleView.setText(doubt.getTitle());
        authorView.setText(doubt.getAuthor());
        numStarsView.setText(String.valueOf(doubt.getLikesCount()));
        //TODO limitar el nombre de palabras cuando se muestra en el HOME
        bodyView.setText(doubt.getDescription());
        starView.setOnClickListener(starClickListener);
    }
}
