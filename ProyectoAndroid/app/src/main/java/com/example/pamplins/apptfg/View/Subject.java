package com.example.pamplins.apptfg.View;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.pamplins.apptfg.R;

/**
 * Created by Gustavo on 22/04/2018.
 */

public class Subject extends AppCompatActivity {
    private String subject;
    private final String [] courses = {"1o", "2o", "3o", "4o"};
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        if(getIntent() != null)
        {
            subject = getIntent().getStringExtra("info");
        }
        initToolbar();

    }

    private void initToolbar(){
        Toolbar myToolbar = findViewById(R.id.tool_subject);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ASIGNATURAS DE " + courses[Integer.valueOf(subject)]);
        myToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorButton), PorterDuff.Mode.SRC_ATOP);
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
