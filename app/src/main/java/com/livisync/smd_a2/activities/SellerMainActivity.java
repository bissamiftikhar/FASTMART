package com.livisync.smd_a2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.fragments.SellerAccountFragment;
import com.livisync.smd_a2.fragments.SellerHomeFragment;
import com.livisync.smd_a2.fragments.SellerOrderHistoryFragment;
import com.livisync.smd_a2.utils.SessionManager;

/**
 * Main activity for Seller dashboard.
 * Displays DrawerLayout with navigation menu and FloatingActionButton to add products.
 * Supports light/dark theme switching.
 */
public class SellerMainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private FloatingActionButton fabAddProduct;
    private SessionManager sessionManager;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme before setting content view
        applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);

        // Initialize
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fabAddProduct = findViewById(R.id.fab_add_product);
        sessionManager = new SessionManager(this);
        prefs = getSharedPreferences("app.settings", MODE_PRIVATE);

        // Setup toolbar and drawer toggle
        setSupportActionBar(findViewById(R.id.toolbar));
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.drawer_open, R.string.drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update drawer header with seller info
        updateDrawerHeader();

        // Setup navigation menu item click listener
        navigationView.setNavigationItemSelectedListener(item -> onNavigationItemSelected(item));

        // FloatingActionButton to add new product
        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(SellerMainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Update drawer header with seller name and email from SharedPrefs
     */
    private void updateDrawerHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvSellerName = headerView.findViewById(R.id.tv_seller_name);
        TextView tvSellerEmail = headerView.findViewById(R.id.tv_seller_email);

        String name = sessionManager.getName();
        String email = prefs.getString("user.email", "No email");

        if (tvSellerName != null) {
            tvSellerName.setText(name != null ? name : "Seller");
        }
        if (tvSellerEmail != null) {
            tvSellerEmail.setText(email);
        }
    }

    /**
     * Handle navigation menu item selection
     */
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Load SellerHomeFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SellerHomeFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_orders) {
            // Load SellerOrderHistoryFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SellerOrderHistoryFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_account) {
            // Load SellerAccountFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SellerAccountFragment())
                    .addToBackStack(null)
                    .commit();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    /**
     * Setup drawer menu with theme toggle and logout
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.seller_drawer_menu, menu);
        return true;
    }

    /**
     * Apply theme based on SharedPrefs setting
     */
    private void applyTheme() {
        prefs = getSharedPreferences("app.settings", MODE_PRIVATE);
        String theme = prefs.getString("theme", "light");
        
        if ("dark".equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Toggle between light and dark theme
     */
    public void toggleTheme() {
        String currentTheme = prefs.getString("theme", "light");
        String newTheme = "dark".equals(currentTheme) ? "light" : "dark";
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("theme", newTheme);
        editor.apply();

        // Restart activity to apply theme
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
