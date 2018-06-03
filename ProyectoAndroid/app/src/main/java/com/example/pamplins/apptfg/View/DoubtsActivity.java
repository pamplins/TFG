package com.example.pamplins.apptfg.View;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.pamplins.apptfg.Controller.Controller;
import com.example.pamplins.apptfg.HoldersAdapters.DoubtAdapterAct;
import com.example.pamplins.apptfg.Model.Doubt;
import com.example.pamplins.apptfg.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gtenorio on 20/05/2018.
 */

public class DoubtsActivity extends AppCompatActivity {
    private RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubts);
        Controller ctrl = Controller.getInstance();
        mRecycler = findViewById(R.id.rv_doubts);
        List<String> doubtNames = new ArrayList<>();
        HashMap<String, Doubt> hashMap = new HashMap<>();
        String response = "";
        String subject = "";
        if(getIntent() != null)
        {
            response = getIntent().getStringExtra("subject");
        }
        if(!response.equals("")){
            response = response.replace("[","");
            response = response.replace("]","");
            String [] aux = response.split(",");
            subject = aux[0];
            for(int i = 1; i < aux.length; i++){
                doubtNames.add(aux[i].trim());
            }
            showDoubts(doubtNames, hashMap, ctrl);
        }
        initToolbar(subject);


    }
    public void showDoubts(final List<String> doubtNames, final HashMap<String, Doubt> hashMap, Controller ctrl) {
        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        for (final String key : doubtNames) {
            ctrl.getDoubtsRef().child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Doubt doubt = dataSnapshot.getValue(Doubt.class);
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
                            DoubtAdapterAct adapter= new DoubtAdapterAct(listDoubts, doubtNames, DoubtsActivity.this);
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

    private void initToolbar(String subject){
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
