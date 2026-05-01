package com.livisync.smd_a2.fragments.buyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.activities.LoginActivity;
import com.livisync.smd_a2.utils.SessionManager;

public class BuyerAccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        SessionManager session = new SessionManager(requireContext());
        String uid = session.getUid();

        TextView tvName = view.findViewById(R.id.tvAccName);
        TextView tvAddress = view.findViewById(R.id.tvAccAddress);
        TextView tvCountry = view.findViewById(R.id.tvAccCountry);
        TextView tvDob = view.findViewById(R.id.tvAccDob);
        TextView tvGender = view.findViewById(R.id.tvAccGender);
        TextView tvPhone = view.findViewById(R.id.tvAccPhone);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Load user info from Firebase
        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .get().addOnSuccessListener(snapshot -> {
                    tvName.setText(snapshot.child("name").getValue(String.class));
                    tvAddress.setText(snapshot.child("address").getValue(String.class));
                    tvCountry.setText(snapshot.child("country").getValue(String.class));
                    tvDob.setText(snapshot.child("dob").getValue(String.class));
                    tvGender.setText(snapshot.child("gender").getValue(String.class));
                    tvPhone.setText(snapshot.child("phone").getValue(String.class));
                });

        btnLogout.setOnClickListener(v -> {
            // Remove user from Firebase and clear session
            FirebaseDatabase.getInstance().getReference("users").child(uid).removeValue();
            FirebaseAuth.getInstance().signOut();
            session.clearSession();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }
}