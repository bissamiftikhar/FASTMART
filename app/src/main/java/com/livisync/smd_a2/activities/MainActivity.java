package com.livisync.smd_a2.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.fragments.CartFragment;
import com.livisync.smd_a2.fragments.FavouritesFragment;
import com.livisync.smd_a2.fragments.HomeFragment;
import com.livisync.smd_a2.fragments.ProfileFragment;
import com.livisync.smd_a2.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_search) {
                fragment = new SearchFragment();
            } else if (id == R.id.nav_favourites) {
                fragment = new FavouritesFragment();
            } else if (id == R.id.nav_cart) {
                fragment = new CartFragment();
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
