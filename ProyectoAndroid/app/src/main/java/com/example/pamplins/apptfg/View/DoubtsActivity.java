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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class DoubtsActivity extends AppCompatActivity {
    private String subject;
    private String response;
    private LinearLayoutManager mManager;
    private Controller ctrl;
    private RecyclerView mRecycler;
    private String [] aux;
    private List<String> doubtNames;
    private HashMap<String, Doubt> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubts);
        ctrl = Controller.getInstance();
        mRecycler = findViewById(R.id.rv_doubts);
        doubtNames = new ArrayList<>();
        hashMap = new HashMap<>();

        if(getIntent() != null)
        {
            response = getIntent().getStringExtra("subject");
        }
        response = response.replace("[","");
        response = response.replace("]","");

        aux = response.split(",");
        subject = aux[0];

        for(int i = 1; i < aux.length; i++){
            doubtNames.add(aux[i].trim());
        }

        initToolbar();

        showDoubts();

    }
    public void showDoubts() {
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        for (final String key : doubtNames) {
            ctrl.getDoubtsRef().child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Doubt doubt = dataSnapshot.getValue(Doubt.class);
                    try {
                        if (null != doubt.getTitle()) {
                            if (!hashMap.keySet().contains(dataSnapshot.getKey())) {
                                hashMap.put(dataSnapshot.getKey(), doubt);
                            } else {
                                hashMap.put(dataSnapshot.getKey(), doubt);
                            }
                        }
                        if (hashMap.keySet().size() == doubtNames.size()) {
                            List listDoubts = new ArrayList<>(hashMap.values());
                            List doubtNames = new ArrayList<>(hashMap.keySet());
                            DoubtAdapterAct adapter = new DoubtAdapterAct(listDoubts, doubtNames, DoubtsActivity.this);
                            mRecycler.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            mRecycler.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            }
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
