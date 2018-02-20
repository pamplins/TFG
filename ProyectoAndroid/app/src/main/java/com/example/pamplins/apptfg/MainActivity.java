package com.example.pamplins.apptfg;

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
import com.example.pamplins.apptfg.Fragments.SearchFragment;

/**
 * Created by Gustavo on 17/02/2018.
 */

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Fragment homeFragment = new HomeFragment();
        final Fragment searchFragment = new SearchFragment();
        final Fragment newDoubtFragment = new NewDoubtFragment();
        final Fragment profileFragment = new ProfileFragment();

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();
        }

        bottomNavigationView =  findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();

                if (item.getItemId() == R.id.inicioItem) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();
                } else if (item.getItemId() == R.id.buscarItem) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, searchFragment).commit();
                } else if (item.getItemId() == R.id.favoritosItem) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, newDoubtFragment).commit();
                } else if (item.getItemId() == R.id.perfilItem) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, profileFragment).commit();
                }
                return true;
            }
        });
    }
}
