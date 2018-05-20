package com.example.pamplins.apptfg.View;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.DoubtAdapterAct;
import com.example.pamplins.apptfg.HoldersAdapters.SubjectAdapter;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.Model.Subject;
import com.example.pamplins.apptfg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class DoubtsActivity extends AppCompatActivity {
    private String subject;
    private LinearLayoutManager mManager;
    private Controller ctrl;
    private List<Doubt> listDoubts;
    private List<String> keys;

    private RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubts);
        ctrl = Controller.getInstance();
        mRecycler = findViewById(R.id.rv_doubts);
        listDoubts = new ArrayList<>();
        keys = new ArrayList<>();

        if(getIntent() != null)
        {
            subject = getIntent().getStringExtra("subject");
        }
        initToolbar();

        showDoubts();

    }
    public void showDoubts() {

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        ctrl.getSubjectsRef().child(subject).child("doubts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String doubtName = snapshot.getValue(String.class);
                    keys.add(doubtName);

                    ctrl.getDoubtsRef().child(doubtName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Doubt doubt = dataSnapshot.getValue(Doubt.class);
                            if(null != doubt.getTitle()){
                                listDoubts.add(doubt);
                            }
                            if(listDoubts.size() == keys.size()){

                                DoubtAdapterAct adapter = new DoubtAdapterAct(listDoubts, keys, DoubtsActivity.this);
                                mRecycler.setAdapter(adapter);
                                mRecycler.setVisibility(View.VISIBLE);

                                adapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void initToolbar(){
        Toolbar myToolbar = findViewById(R.id.tool_doubts);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(subject);
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
