package com.livisync.smd_a2.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.utils.SessionManager;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPhone, etAddress, etCountry, etDob;
    private RadioGroup rgGender;
    private Spinner spAccountType;
    private Button btnSaveProfile;
    private FirebaseAuth mAuth;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        session = new SessionManager(this);

        etName = findViewById(R.id.etSignupName);
        etEmail = findViewById(R.id.etSignupEmail);
        etPassword = findViewById(R.id.etSignupPassword);
        etPhone = findViewById(R.id.etSignupPhone);
        etAddress = findViewById(R.id.etSignupAddress);
        etCountry = findViewById(R.id.etSignupCountry);
        etDob = findViewById(R.id.etSignupDob);
        rgGender = findViewById(R.id.rgGender);
        spAccountType = findViewById(R.id.spAccountType);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Account type spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Buyer", "Seller"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccountType.setAdapter(adapter);

        // DOB picker
        etDob.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) ->
                    etDob.setText(d + "/" + (m + 1) + "/" + y),
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSaveProfile.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String gender = rgGender.getCheckedRadioButtonId() == R.id.rbMale ? "Male" : "Female";
        String accountType = spAccountType.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = mAuth.getCurrentUser().getUid();

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("uid", uid);
                userMap.put("name", name);
                userMap.put("email", email);
                userMap.put("phone", phone);
                userMap.put("address", address);
                userMap.put("country", country);
                userMap.put("dob", dob);
                userMap.put("gender", gender);
                userMap.put("accountType", accountType);

                FirebaseDatabase.getInstance().getReference("users")
                        .child(uid).setValue(userMap).addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                session.saveSession(uid, name, accountType);
                                if ("Seller".equals(accountType)) {
                                    startActivity(new Intent(this, SellerMainActivity.class));
                                } else {
                                    startActivity(new Intent(this, MainActivity.class));
                                }
                                finish();
                            }
                        });
            } else {
                Toast.makeText(this, "Signup failed: " + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}