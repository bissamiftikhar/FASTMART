package com.livisync.smd_a2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.activities.LoginActivity;
import com.livisync.smd_a2.utils.SessionManager;

/**
 * Fragment displaying seller account information.
 * Shows user details from Firebase and provides logout functionality.
 */
public class SellerAccountFragment extends Fragment {
    private TextView tvSellerName, tvEmail, tvPhone, tvAddress, tvCountry, tvGender, tvDob;
    private Button btnLogout;
    private ProgressBar progressBar;
    private DatabaseReference usersRef;
    private SessionManager sessionManager;
    private FirebaseAuth firebaseAuth;
    private String currentUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_account, container, false);

        // Initialize
        tvSellerName = view.findViewById(R.id.tv_seller_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvAddress = view.findViewById(R.id.tv_address);
        tvCountry = view.findViewById(R.id.tv_country);
        tvGender = view.findViewById(R.id.tv_gender);
        tvDob = view.findViewById(R.id.tv_dob);
        btnLogout = view.findViewById(R.id.btn_logout);
        progressBar = view.findViewById(R.id.progressBar);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        sessionManager = new SessionManager(requireContext());
        firebaseAuth = FirebaseAuth.getInstance();
        currentUid = sessionManager.getUid();

        // Load user details
        loadUserDetails();

        // Logout button click listener
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());

        return view;
    }

    /**
     * Load seller details from Firebase Realtime Database
     */
    private void loadUserDetails() {
        progressBar.setVisibility(View.VISIBLE);
        usersRef.child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String country = snapshot.child("country").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);

                    tvSellerName.setText(name != null ? name : "N/A");
                    tvEmail.setText(email != null ? email : "N/A");
                    tvPhone.setText(phone != null ? phone : "N/A");
                    tvAddress.setText(address != null ? address : "N/A");
                    tvCountry.setText(country != null ? country : "N/A");
                    tvGender.setText(gender != null ? gender : "N/A");
                    tvDob.setText(dob != null ? dob : "N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error loading details: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show logout confirmation dialog
     */
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Perform logout: sign out from Firebase, clear SharedPrefs, delete user node, and navigate to Login
     */
    private void performLogout() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Delete user node from Firebase
        usersRef.child(currentUid).removeValue((error, ref) -> {
            // Sign out from Firebase Auth
            firebaseAuth.signOut();

            // Clear SharedPrefs
            sessionManager.clearSession();

            progressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate to LoginActivity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}
