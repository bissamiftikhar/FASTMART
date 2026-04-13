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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.activities.MainActivity;

public class LoginFragment extends Fragment {
    private SharedPreferences prefs;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        
        prefs = requireActivity().getSharedPreferences("app.settings", MODE_PRIVATE);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

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

            String savedEmail = prefs.getString("user.email", "");
            String savedPassword = prefs.getString("user.password", "");

            if (email.equals(savedEmail) && password.equals(savedPassword)) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("user.isLoggedIn", true);
                editor.apply();

                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(requireActivity(), "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
        
        return view;
    }
}
