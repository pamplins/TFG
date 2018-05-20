package com.example.pamplins.apptfg.HoldersAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pamplins.apptfg.Model.Subject;
import com.example.pamplins.apptfg.R;

import java.util.List;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class SubjectAdapter extends RecyclerView.Adapter<SubjectViewHolder> {

    private List<Subject> subjects;
    private List<String> keys;

    public SubjectAdapter(List<Subject> subjects, List<String> keys) {
        this.subjects = subjects;
        this.keys = keys;
    }
    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subject, viewGroup, false);
        return new SubjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO abrir pantalla de dudas de la asignatura seleccionada
            }
        });

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
