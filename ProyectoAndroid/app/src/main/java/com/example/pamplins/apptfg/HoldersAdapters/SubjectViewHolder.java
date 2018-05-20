package com.example.pamplins.apptfg.HoldersAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.pamplins.apptfg.Model.Subject;
import com.example.pamplins.apptfg.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class SubjectViewHolder extends RecyclerView.ViewHolder{

    private TextView name;
    private TextView course;
    private TextView nDoubts;

    public SubjectViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name_subject);
        course = itemView.findViewById(R.id.course_subject);
        nDoubts = itemView.findViewById(R.id.num_doubts);

    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getCourse() {
        return course;
    }

    public void setCourse(TextView course) {
        this.course = course;
    }

    public TextView getnDoubts() {
        return nDoubts;
    }

    public void setnDoubts(TextView nDoubts) {
        this.nDoubts = nDoubts;
    }
}
