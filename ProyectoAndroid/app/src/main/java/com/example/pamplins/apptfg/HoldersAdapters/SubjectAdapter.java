package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Model.Subject;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.View.DoubtDetailActivity;
import com.example.pamplins.apptfg.View.DoubtsActivity;

import java.util.List;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class SubjectAdapter extends RecyclerView.Adapter<SubjectViewHolder> {

    private List<Subject> subjects;
    private List<String> keys;
    private Activity activity;

    public SubjectAdapter(List<Subject> subjects, List<String> keys, Activity activity) {
        this.subjects = subjects;
        this.keys = keys;
        this.activity = activity;
    }
    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subject, viewGroup, false);
        return new SubjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, final int position) {
        final Subject subject = subjects.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!subject.getDoubts().get(0).equals("")){
                    Intent intent = new Intent(activity, DoubtsActivity.class);
                    intent.putExtra("subject", keys.get(position)+","+subjects.get(position).getDoubts().toString());
                    activity.startActivity(intent);
                }else{
                    Snackbar.make(activity.findViewById(android.R.id.content), "Esta asignatura no dispone de ninguna duda", Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });
        // TODO borrar asigatura al mantener presionada set on large
        String course = subject.getCourse() + " - " + subject.getSemester();
        holder.getCourse().setText(course);

        holder.getName().setText(keys.get(position));
        if(subject.getDoubts().get(0).equals("")){
            holder.getnDoubts().setText(Integer.toString(0));
        }else{
            holder.getnDoubts().setText(Integer.toString(subject.getDoubts().size()));
        }

    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }
}
