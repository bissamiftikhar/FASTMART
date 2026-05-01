package com.livisync.smd_a2.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.fragments.buyer.*;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigationView);

        // Load home by default
        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_home) fragment = new HomeFragment();
            else if (id == R.id.nav_favourites) fragment = new FavouritesFragment();
            else if (id == R.id.nav_cart) fragment = new CartFragment();
            else if (id == R.id.nav_account) fragment = new BuyerAccountFragment();
            else return false;
            loadFragment(fragment);
            return true;
        });

        // Chat FAB
        FloatingActionButton fabChat = findViewById(R.id.fabChat);
        fabChat.setOnClickListener(v ->
                startActivity(new Intent(this, ChatActivity.class)));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}