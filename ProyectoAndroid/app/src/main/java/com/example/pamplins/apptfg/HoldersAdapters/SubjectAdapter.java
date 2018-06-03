package com.example.pamplins.apptfg.HoldersAdapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pamplins.apptfg.Constants;
import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Subject;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;
import com.example.pamplins.apptfg.View.DoubtDetailActivity;
import com.example.pamplins.apptfg.View.DoubtsActivity;

import java.util.List;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private List<Subject> subjects;
    private List<String> keys;
    private Activity activity;
    private Controller ctrl;

    public SubjectAdapter(List<Subject> subjects, List<String> keys, Activity activity, Controller ctrl) {
        this.subjects = subjects;
        this.keys = keys;
        this.activity = activity;
        this.ctrl = ctrl;
    }
    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subject, viewGroup, false);
        return new SubjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SubjectViewHolder holder, final int position) {
        final Subject subject = subjects.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!subject.getDoubts().get(0).equals("")){
                    Intent intent = new Intent(activity, DoubtsActivity.class);
                    intent.putExtra("subject", keys.get(position)+","+subjects.get(position).getDoubts().toString());
                    activity.startActivity(intent);
                }else{
                    Snackbar.make(activity.findViewById(android.R.id.content), R.string.err_empty_subject, Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeSubject(holder.getAdapterPosition());
                return true;
            }
        });
        String course = subject.getCourse() + " - " + subject.getSemester();
        holder.course.setText(course);

        holder.name.setText(keys.get(position));
        if(subject.getDoubts().get(0).equals("")){
            holder.nDoubts.setText(Integer.toString(0));
        }else{
            holder.nDoubts.setText(Integer.toString(subject.getDoubts().size()));
        }

    }

    private void removeSubject(final int adapterPosition) {
        new AlertDialog.Builder(activity)
            .setMessage(R.string.alert_remove_subject)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ctrl.updateUserSubjects(keys.get(adapterPosition), false);
                    keys.remove(adapterPosition);
                    subjects.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                    notifyItemRangeChanged(adapterPosition, subjects.size());

                }})
            .setNegativeButton(R.string.not, null).show();

    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView course;
        TextView nDoubts;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_subject);
            course = itemView.findViewById(R.id.course_subject);
            nDoubts = itemView.findViewById(R.id.num_doubts);
        }
    }
}
