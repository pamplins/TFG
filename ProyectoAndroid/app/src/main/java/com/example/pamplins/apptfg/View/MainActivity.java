package com.example.pamplins.apptfg.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.pamplins.apptfg.Fragments.HomeFragment;
import com.example.pamplins.apptfg.Fragments.NewDoubtFragment;
import com.example.pamplins.apptfg.Fragments.ProfileFragment;
import com.example.pamplins.apptfg.Fragments.MySubjectsFragment;
import com.example.pamplins.apptfg.R;

/**
 * Created by Gustavo on 17/02/2018.
 */


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Fragment homeFragment = new HomeFragment();
        final Fragment mySubjectsFragment = new MySubjectsFragment();
        final Fragment newDoubtFragment = new NewDoubtFragment();
        final Fragment profileFragment = new ProfileFragment();
        bottomNavigationView =  findViewById(R.id.bottomNavigationView);

        if (getIntent().hasExtra("main"))
        {
            String prueba = getIntent().getExtras().getString("main");
            Bundle bundle = new Bundle();
            bundle.putString("subjects", prueba);
            mySubjectsFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, mySubjectsFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.my_subjects_item);
            getIntent().removeExtra("main");

        }
        else{
            if(savedInstanceState == null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();
            }
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (item.getItemId() == R.id.home_item) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();
                } else if (item.getItemId() == R.id.my_subjects_item) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, mySubjectsFragment).commit();
                } else if (item.getItemId() == R.id.doubt_item) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, newDoubtFragment).commit();
                } else if (item.getItemId() == R.id.profile_item) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, profileFragment).commit();
                }
                return true;
            }
        });
    }
}
