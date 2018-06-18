package com.example.pamplins.apptfg.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Model.Course;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Gustavo on 22/04/2018.
 */

public class SubjectActivity extends AppCompatActivity {
    private String subject;
    private final String [] courses = {"1o", "2o", "3o", "4o", "Optativas", "Otros"};
    private ArrayList<String> subjects;
    private ArrayList<String> firstSemester;
    private ArrayList<String> secondSemester;

    private LinearLayout linearSubjects;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        subjects = new ArrayList<>();

        if(getIntent() != null)
        {
            subject = getIntent().getStringExtra("course");
        }
        initToolbar();
        getSubjects();

    }

    private void getSubjects() {
        String nameCourse = courses[Integer.valueOf(subject)];
        Controller.getInstance().getCoursesRef().child(nameCourse).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Course course = dataSnapshot.getValue(Course.class);
                firstSemester = course.getSubjects().get("1r semestre");
                secondSemester = course.getSubjects().get("2o semestre");

                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                linearSubjects = findViewById(R.id.linear_subjects);
                if(null != firstSemester){
                    createCardsViews(getResources().getString(R.string.first_semester), firstSemester, params);
                }
                if(null != secondSemester){
                    createCardsViews(getResources().getString(R.string.second_semester), secondSemester, params);
                }
                setSingleEvent();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createCardsViews(String sem, ArrayList<String> semester, LayoutParams params) {
        TextView tv = new TextView(getApplicationContext());
        tv.setPadding(30,16,16,16);
        tv.setLayoutParams(params);
        tv.setText(sem);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tv.setTextColor(getResources().getColor(R.color.colorName));
        linearSubjects.addView(tv);

        for(String subjectName : semester){
            CardView card = new CardView(getApplicationContext());
            int margin = 30;
            params.setMargins(margin, margin, margin, margin);
            card.setLayoutParams(params);
            card.setRadius(8);
            if (Controller.getInstance().getUser().getSubjects().contains(subjectName)) {
                card.setCardBackgroundColor(getResources().getColor(R.color.cardViewDisabled));
                card.setAlpha(0.5f);
                card.setEnabled(false);
            } else {
                card.setCardBackgroundColor(getResources().getColor(R.color.backGround));
            }


            card.setMaxCardElevation(15);
            card.setCardElevation(9);
            tv = new TextView(getApplicationContext());
            tv.setLayoutParams(params);
            tv.setText(subjectName);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            tv.setTextColor(getResources().getColor(R.color.colorButton));
            card.addView(tv);

            linearSubjects.addView(card);
        }

    }

    private void initToolbar(){
        Toolbar myToolbar = findViewById(R.id.tool_subject);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(courses[Integer.valueOf(subject)]);
        myToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorButton), PorterDuff.Mode.SRC_ATOP);
    }

    private void setSingleEvent() {
        for (int i = 0; i < linearSubjects.getChildCount(); i++) {

            if (linearSubjects.getChildAt(i).getClass().toString().contains("CardView")) {

                final CardView cardView = (CardView) linearSubjects.getChildAt(i);

                final int finalI = i;
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float alpha = cardView.getAlpha();
                        String nameSubject = ((TextView)cardView.getChildAt(0)).getText().toString();

                        if (alpha != 0.5f) {
                            cardView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.cardViewPressed));
                            cardView.setAlpha(0.5f);
                            subjects.add(nameSubject);
                        } else if (alpha == 0.5f) {
                            cardView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.backGround));
                            cardView.setAlpha(1f);
                        if(subjects.contains(nameSubject)){
                            subjects.remove(nameSubject);
                        }
                        }
                    }
                });

            }
        }
    }

    public void addSubject(View v){
        v.startAnimation(Utils.getButtonAnimation());
        if(!subjects.isEmpty()){
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("main", subjects.toString());
            startActivity(i);
            finish();
        }else{
            Snackbar.make(v, R.string.select_any_subject, Snackbar.LENGTH_SHORT).show();
        }

    }

    private void confirmExit(){
        if(!subjects.isEmpty()){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.exit_with_selected_subjects)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }})
                    .setNegativeButton(R.string.not, null).show();
        }
        else{
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        confirmExit();
        return true;
    }
    @Override
    public void onBackPressed() {
        confirmExit();
    }

}
