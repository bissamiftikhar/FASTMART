package com.livisync.smd_a2.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.activities.MainActivity;
import com.livisync.smd_a2.utils.SessionManager;

import java.util.Calendar;
import java.util.Locale;

public class SignUpFragment extends Fragment {

    // Step 1 views
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnNextStep;
    private View step1Container;

    // Step 2 views
    private EditText etName, etPhoneNumber, etAddress, etDateOfBirth;
    private RadioGroup rgGender;
    private Spinner spAccountType, spCountry;
    private Button btnCompleteSignUp;
    private View step2Container;
    private ProgressBar progressBar;

    // Firebase references
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private SharedPreferences prefs;
    private SessionManager sessionManager;

    private String tempEmail, tempPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        prefs = requireActivity().getSharedPreferences("app.settings", Context.MODE_PRIVATE);
        sessionManager = new SessionManager(requireContext());

        step1Container = view.findViewById(R.id.step1_container);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnNextStep = view.findViewById(R.id.btnNextStep);
        progressBar = view.findViewById(R.id.progressBar);

        step2Container = view.findViewById(R.id.step2_container);
        etName = view.findViewById(R.id.etName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etAddress = view.findViewById(R.id.etAddress);
        etDateOfBirth = view.findViewById(R.id.etDateOfBirth);
        rgGender = view.findViewById(R.id.rgGender);
        spAccountType = view.findViewById(R.id.spAccountType);
        spCountry = view.findViewById(R.id.spCountry);
        btnCompleteSignUp = view.findViewById(R.id.btnCompleteSignUp);

        setupSpinners();
        setupDatePicker();

        btnNextStep.setOnClickListener(v -> validateAndCreateAccount());
        btnCompleteSignUp.setOnClickListener(v -> completeSignUp());

        return view;
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> accountAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.account_types, android.R.layout.simple_spinner_item);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccountType.setAdapter(accountAdapter);

        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.countries, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountry.setAdapter(countryAdapter);
    }

    private void setupDatePicker() {
        etDateOfBirth.setFocusable(false);
        etDateOfBirth.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        etDateOfBirth.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void validateAndCreateAccount() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty()) { etEmail.setError("Email required"); return; }
        if (password.length() < 6) { etPassword.setError("Min 6 chars"); return; }
        if (!password.equals(confirmPassword)) { etConfirmPassword.setError("No match"); return; }

        tempEmail = email;
        tempPassword = password;

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        step1Container.setVisibility(View.GONE);
                        step2Container.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(requireContext(), "Auth Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void completeSignUp() {
        String name = etName.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDateOfBirth.getText().toString().trim();
        String accountType = spAccountType.getSelectedItem().toString();
        
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == -1 || name.isEmpty() || phone.isEmpty() || address.isEmpty() || dob.isEmpty()) {
            Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String gender = ((RadioButton) step2Container.findViewById(selectedGenderId)).getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        String uid = firebaseAuth.getCurrentUser().getUid();

        UserProfile profile = new UserProfile(uid, name, tempEmail, phone, address,
                spCountry.getSelectedItem().toString(), gender, dob, accountType);

        usersRef.child(uid).setValue(profile).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                sessionManager.saveSession(uid, name, accountType);
                startActivity(new Intent(requireActivity(), MainActivity.class));
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class UserProfile {
        public String uid, name, email, phone, address, country, gender, dob, accountType;
        public UserProfile() {}
        public UserProfile(String uid, String name, String email, String phone, String address,
                          String country, String gender, String dob, String accountType) {
            this.uid = uid; this.name = name; this.email = email; this.phone = phone;
            this.address = address; this.country = country; this.gender = gender;
            this.dob = dob; this.accountType = accountType;
        }
    }
}
