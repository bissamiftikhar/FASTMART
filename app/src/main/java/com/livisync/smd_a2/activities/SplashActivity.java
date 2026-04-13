package com.livisync.smd_a2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.livisync.smd_a2.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        SharedPreferences prefs = getSharedPreferences("app.settings", MODE_PRIVATE);
        boolean onboardingShown = prefs.getBoolean("onboarding.shown", false);
        boolean isLoggedIn = prefs.getBoolean("user.isLoggedIn", false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else if (onboardingShown) {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}
