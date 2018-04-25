package com.example.pamplins.apptfg.View;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pamplins.apptfg.R;

import java.util.ArrayList;

/**
 * Created by Gustavo on 22/04/2018.
 */

public class Subject extends AppCompatActivity {
    private String subject;
    private final String [] courses = {"1o", "2o", "3o", "4o"};
    private ArrayList<String> subjects;
    private GridLayout mainGrid;
    private GridLayout mainGrid2;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        if(getIntent() != null)
        {
            subject = getIntent().getStringExtra("course");
        }
        initToolbar();
        mainGrid = findViewById(R.id.grid_subject_1);
        mainGrid2 = findViewById(R.id.grid_subject_2);

        setSingleEvent(mainGrid);
        setSingleEvent(mainGrid2);
        subjects = new ArrayList<>();

    }

    private void initToolbar(){
        Toolbar myToolbar = findViewById(R.id.tool_subject);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ASIGNATURAS DE " + courses[Integer.valueOf(subject)]);
        myToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorButton), PorterDuff.Mode.SRC_ATOP);
    }
    //TODO devolver nombre de las asignaturas Y selecionar todo. para cada una de los semestres
    private void setSingleEvent(GridLayout mainGrid) {
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cardView.getAlpha() == 0.5f) {
                        cardView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.backGround));
                        cardView.setAlpha(1f);
                        String course = ((TextView)((LinearLayout) cardView.getChildAt(0)).getChildAt(0)).getText().toString();

                        if(subjects.contains(course)){
                           subjects.remove(course);
                        }
                    }

                }
            });

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (cardView.getAlpha() != 0.5f) {
                        cardView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.cardViewPressed));
                        cardView.setAlpha(0.5f);
                        String course = ((TextView)((LinearLayout) cardView.getChildAt(0)).getChildAt(0)).getText().toString();
                        subjects.add(course);
                    }
                    return true;
                }

            });
        }
    }

    public void addSubject(View v){
        Intent openFragmentBIntent = new Intent(this, MainActivity.class);
        openFragmentBIntent.putExtra("main", subjects.toString());
        startActivity(openFragmentBIntent);
        finish();
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
