package com.livisync.smd_a2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.adapters.CartAdapter;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.TotalUpdateListener {

    private RecyclerView rvCart;
    private TextView tvTotal;
    private Button btnCheckout;
    private SharedPreferences prefs;
    private List<Integer> cartPositions;
    private List<Integer> quantities;
    private CartAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        prefs = requireActivity().getSharedPreferences("app.settings", Context.MODE_PRIVATE);

        rvCart = view.findViewById(R.id.rvCart);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        cartPositions = new ArrayList<>();
        quantities = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            if (prefs.getBoolean("cart." + i, false)) {
                cartPositions.add(i);
                quantities.add(prefs.getInt("cart.qty." + i, 1));
            }
        }

        adapter = new CartAdapter(requireContext(), cartPositions, quantities, this);
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(adapter);

        onTotalUpdated();

        btnCheckout.setOnClickListener(v -> {
            if (cartPositions.isEmpty()) {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            sendOrderSMS();
        });

        return view;
    }

    @Override
    public void onTotalUpdated() {
        double total = 0;
        for (int i = 0; i < cartPositions.size(); i++) {
            int productIndex = cartPositions.get(i);
            String priceStr = prefs.getString("cart.price." + productIndex, "0");
            try {
                double price = Double.parseDouble(priceStr);
                total += price * quantities.get(i);
            } catch (NumberFormatException e) {
                // Ignore invalid prices
            }
        }
        tvTotal.setText(String.format("$%.2f", total));
    }

    private void sendOrderSMS() {
        StringBuilder orderDetails = new StringBuilder("FastMart Order:\n");
        double total = 0;

        for (int i = 0; i < cartPositions.size(); i++) {
            int productIndex = cartPositions.get(i);
            String name = prefs.getString("cart.name." + productIndex, "");
            String priceStr = prefs.getString("cart.price." + productIndex, "0");
            int qty = quantities.get(i);

            try {
                double price = Double.parseDouble(priceStr);
                orderDetails.append(name)
                        .append(" x").append(qty)
                        .append(" = $").append(String.format("%.2f", price * qty))
                        .append("\n");
                total += price * qty;
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        orderDetails.append("Total: $").append(String.format("%.2f", total));

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    "03001234567",
                    null,
                    orderDetails.toString(),
                    null,
                    null
            );
            Toast.makeText(requireContext(), "Order placed via SMS!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to send SMS", Toast.LENGTH_SHORT).show();
        }
    }
}
