package com.srini.vcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private NavigationBarView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected=null;
                switch (item.getItemId()){
                    case R.id.nav_home: {
                        selected = new HomeFragment();
                        break;
                    }
                   
                    case R.id.nav_profile:{
                        selected = new ProfileFragment();
                        break;
                    }
                    default:{
                        selected = null;
                        break;
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selected).commit();
                return true;
            }
        });

    }
}