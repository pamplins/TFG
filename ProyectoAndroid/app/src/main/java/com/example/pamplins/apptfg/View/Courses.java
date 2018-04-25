package com.example.pamplins.apptfg.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.Fragments.MySubjectsFragment;
import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.View.Subject;

import java.util.ArrayList;

/**
 * Created by Gustavo on 21/04/2018.
 */

public class Courses extends AppCompatActivity {
    private GridLayout mainGrid;
    private ArrayList<String> courses;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        initElements();
    }

    private void initElements(){
        initToolbar();
        mainGrid = findViewById(R.id.mainGrid);
        setSingleEvent(mainGrid);
        courses = new ArrayList<>();
    }
    private void initToolbar(){
        Toolbar myToolbar = findViewById(R.id.tool_course);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CURSOS");
        myToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorButton), PorterDuff.Mode.SRC_ATOP);
    }
    private void setSingleEvent(GridLayout mainGrid) {
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Courses.this,Subject.class);
                    intent.putExtra("course",String.valueOf(finalI));
                    startActivity(intent);
                }
            });

        }
    }



    @Override
    public boolean onSupportNavigateUp(){
        if(!courses.isEmpty()){
            //Dialogo de confirmacion de que quiere salir sin haber guardado
        }
        finish();
        return true;
    }
    @Override
    public void onBackPressed() {
        if(!courses.isEmpty()){
            //Dialogo de confirmacion de que quiere salir sin haber guardado
        }
        finish();
    }

}
