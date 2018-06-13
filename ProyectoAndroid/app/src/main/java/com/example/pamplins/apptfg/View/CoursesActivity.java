package com.example.pamplins.apptfg.View;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridLayout;

import com.example.pamplins.apptfg.R;
import com.example.pamplins.apptfg.Utils;

/**
 * Created by Gustavo on 21/04/2018.
 */

public class CoursesActivity extends AppCompatActivity {
    private GridLayout mainGrid;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        initElements();
    }

    private void initElements(){
        initToolbar();
        mainGrid = findViewById(R.id.mainGrid);
        setSingleEvent(mainGrid);
    }
    private void initToolbar(){
        Toolbar myToolbar = findViewById(R.id.tool_course);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.course);
        myToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorButton), PorterDuff.Mode.SRC_ATOP);
    }

    private void setSingleEvent(GridLayout mainGrid) {
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(Utils.getButtonAnimation());
                    Intent intent = new Intent(CoursesActivity.this,SubjectActivity.class);
                    intent.putExtra("course",String.valueOf(finalI));
                    startActivity(intent);
                }
            });

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
