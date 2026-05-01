package com.livisync.smd_a2.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.activities.MainActivity;
import com.livisync.smd_a2.activities.SignupActivity;
import com.livisync.smd_a2.utils.SessionManager;

/**
 * Fragment for user login using Firebase Authentication.
 * Authenticates user email and password, then fetches user details from Realtime DB.
 */
public class LoginFragment extends Fragment {
    private SharedPreferences prefs;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        
        // Initialize Firebase and UI components
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        prefs = requireActivity().getSharedPreferences("app.settings", MODE_PRIVATE);
        sessionManager = new SessionManager(requireContext());
        
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        progressBar = view.findViewById(R.id.progressBar);
        
        if (progressBar == null) {
            progressBar = new ProgressBar(requireContext());
        }

        // Login button click listener
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Invalid email format");
                return;
            }

            // Firebase sign-in
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign-in successful, fetch user details
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                fetchUserDetails(uid);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireActivity(), "Login failed: " + task.getException().getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        
        return view;
    }

    /**
     * Fetch user details from Firebase Realtime Database.
     * Routes to MainActivity for Buyer or SignupActivity for Seller.
     * @param uid User ID
     */
    private void fetchUserDetails(String uid) {
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String accountType = snapshot.child("accountType").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String country = snapshot.child("country").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String dob = snapshot.child("dob").getValue(String.class);

                    // Save to SharedPrefs
                    sessionManager.saveSession(uid, name, accountType);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("user.email", email);
                    editor.putString("user.phone", phone);
                    editor.putString("user.address", address);
                    editor.putString("user.country", country);
                    editor.putString("user.gender", gender);
                    editor.putString("user.dob", dob);
                    editor.apply();

                    // Route to appropriate activity
                    if ("Seller".equals(accountType)) {
                        Intent intent = new Intent(requireActivity(), SignupActivity.class);
                        intent.putExtra("seller_activity", true);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
