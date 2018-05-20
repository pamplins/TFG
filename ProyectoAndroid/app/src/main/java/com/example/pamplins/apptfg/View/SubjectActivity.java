package com.example.pamplins.apptfg.View;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Gustavo on 22/04/2018.
 */

public class SubjectActivity extends AppCompatActivity {
    private String subject;
    private final String [] courses = {"1o", "2o", "3o", "4o"};
    private ArrayList<String> subjects;
    private ArrayList<String> firstSemester;
    private ArrayList<String> secondSemester;

    private LinearLayout linearSubjects;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        if(getIntent() != null)
        {
            subject = getIntent().getStringExtra("course");
        }
        initToolbar();
        getSubjects();

    }

    private void getSubjects() {
        //TODO añadir barra de progreso si no hay nada en linearSubjects
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
                    createCardsViews("1r semestre", firstSemester, params);
                }
                if(null != secondSemester){
                    createCardsViews("2o semestre", secondSemester, params);
                }
                setSingleEvent();
                subjects = new ArrayList<>();

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
            if(Controller.getInstance().getUser().getSubjects().contains(subjectName)) {
                card.setCardBackgroundColor(getResources().getColor(R.color.cardViewDisabled));
                card.setAlpha(0.5f);
                card.setEnabled(false);
            }else{
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
        getSupportActionBar().setTitle("ASIGNATURAS DE " + courses[Integer.valueOf(subject)]);
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
        if(!subjects.isEmpty()){
            Intent openFragmentBIntent = new Intent(this, MainActivity.class);
            openFragmentBIntent.putExtra("main", subjects.toString());
            startActivity(openFragmentBIntent);
            finish();
        }else{
            Snackbar.make(v, "Selecciona alguna asignatura", Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
    }

}
