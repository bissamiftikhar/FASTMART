package com.livisync.smd_a2.fragments.buyer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.adapters.CartAdapter;
import com.livisync.smd_a2.db.CartDbHelper;
import com.livisync.smd_a2.models.CartItem;
import com.livisync.smd_a2.utils.SessionManager;
import com.livisync.smd_a2.viewmodels.CartViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment displaying shopping cart.
 * Manages cart items using SQLite with real-time updates.
 * Supports checkout with SMS notification and Firebase order saving.
 */
public class CartFragment extends Fragment {
    private RecyclerView rvCart;
    private CartAdapter cartAdapter;
    private CartDbHelper cartDbHelper;
    private TextView tvTotal;
    private Button btnCheckout;
    private ProgressBar progressBar;
    private CartViewModel cartViewModel;
    private List<CartItem> cartList;
    private DatabaseReference ordersRef;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize database and Firebase
        cartDbHelper = new CartDbHelper(requireContext());
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        sessionManager = new SessionManager(requireContext());

        // Initialize ViewModel
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Setup UI components
        rvCart = view.findViewById(R.id.rv_cart);
        tvTotal = view.findViewById(R.id.tv_total_price);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        progressBar = view.findViewById(R.id.progressBar);

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(requireContext(), cartList, () -> {
            // Refresh on total changed
            refreshCartData();
        });

        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(cartAdapter);

        // Observe LiveData for cart items and total
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                cartList.clear();
                cartList.addAll(items);
                cartAdapter.notifyDataSetChanged();
            }
        });

        cartViewModel.getTotalPrice().observe(getViewLifecycleOwner(), total -> {
            if (total != null) {
                tvTotal.setText(String.format("Total: $%.2f", total));
            }
        });

        // Checkout button
        btnCheckout.setOnClickListener(v -> performCheckout());

        // Initial load
        refreshCartData();

        return view;
    }

    /**
     * Refresh cart data from database
     */
    private void refreshCartData() {
        List<CartItem> items = cartDbHelper.getAllCartItems();
        cartList.clear();
        cartList.addAll(items);
        cartAdapter.notifyDataSetChanged();

        double total = cartDbHelper.getTotalPrice();
        tvTotal.setText(String.format("Total: $%.2f", total));
    }

    /**
     * Perform checkout: validate cart, send SMS, save order to Firebase
     */
    private void performCheckout() {
        if (cartList.isEmpty()) {
            Toast.makeText(requireContext(), "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Confirm checkout
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Checkout")
                .setMessage("Total: $" + String.format("%.2f", cartDbHelper.getTotalPrice()))
                .setPositiveButton("Proceed", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    checkout();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Process checkout: send SMS and save order
     */
    private void checkout() {
        // Build order summary
        StringBuilder productNames = new StringBuilder();
        StringBuilder summary = new StringBuilder("FastMart Order:\n");
        double totalPrice = 0;

        for (CartItem item : cartList) {
            productNames.append(item.getName()).append(", ");
            summary.append(item.getName())
                    .append(" x").append(item.getQuantity())
                    .append(" = $").append(String.format("%.2f", item.getPrice() * item.getQuantity()))
                    .append("\n");
            totalPrice += item.getPrice() * item.getQuantity();
        }

        summary.append("\nTotal: $").append(String.format("%.2f", totalPrice));

        // Send SMS
        sendSms(summary.toString());

        // Save order to Firebase
        saveOrderToFirebase(productNames.toString().replaceAll(", $", ""), totalPrice);

        progressBar.setVisibility(View.GONE);
        Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_LONG).show();

        // Clear cart
        cartDbHelper.clearCart();
        refreshCartData();
    }

    /**
     * Send SMS with order summary via SmsManager
     * @param message Order summary message
     */
    private void sendSms(String message) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                // Use a default number or retrieve from user settings
                String phoneNumber = "+923001234567"; // Replace with actual phone number
                
                // SMS has 160 character limit, split if needed
                if (message.length() > 160) {
                    ArrayList<String> parts = smsManager.divideMessage(message);
                    smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
                } else {
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                }
                Toast.makeText(requireContext(), "Order SMS sent!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "SMS failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Request SMS permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.SEND_SMS}, 100);
        }
    }

    /**
     * Save order to Firebase Realtime Database
     * @param productNames Comma-separated product names
     * @param totalPrice Total order price
     */
    private void saveOrderToFirebase(String productNames, double totalPrice) {
        String uid = sessionManager.getUid();
        String orderId = "ORD-" + System.currentTimeMillis();
        String timestamp = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(new Date());

        Map<String, Object> order = new HashMap<>();
        order.put("buyerId", uid);
        order.put("productNames", productNames);
        order.put("totalPrice", totalPrice);
        order.put("timestamp", timestamp);
        order.put("status", "PROCESSING");

        ordersRef.child(uid).child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Order saved to database", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save order: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}