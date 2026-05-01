package com.livisync.smd_a2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager session = new SessionManager(this);

        new Handler().postDelayed(() -> {
            if (session.isLoggedIn()) {
                // Go directly to correct home
                String account = session.getAccount();
                if ("Seller".equals(account)) {
                    startActivity(new Intent(this, SellerMainActivity.class));
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }
}