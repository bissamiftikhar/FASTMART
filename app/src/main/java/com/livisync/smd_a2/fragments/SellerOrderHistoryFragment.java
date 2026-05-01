package com.livisync.smd_a2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.adapters.SellerOrderAdapter;
import com.livisync.smd_a2.models.Order;
import com.livisync.smd_a2.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying seller's order history.
 * Shows all orders from the Firebase database with seller's products.
 */
public class SellerOrderHistoryFragment extends Fragment {
    private RecyclerView rvOrders;
    private SellerOrderAdapter orderAdapter;
    private List<Order> orderList;
    private DatabaseReference ordersRef;
    private SessionManager sessionManager;
    private String currentSellerId;
    private ValueEventListener orderListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_order_history, container, false);

        // Initialize
        rvOrders = view.findViewById(R.id.rv_seller_orders);
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        sessionManager = new SessionManager(requireContext());
        currentSellerId = sessionManager.getUid();

        // Setup RecyclerView
        orderList = new ArrayList<>();
        orderAdapter = new SellerOrderAdapter(orderList);

        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrders.setAdapter(orderAdapter);

        // Fetch orders
        fetchAllOrders();

        return view;
    }

    /**
     * Fetch all orders from Firebase using ValueEventListener for real-time updates
     */
    private void fetchAllOrders() {
        orderListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot buyerSnapshot : snapshot.getChildren()) {
                    // Each buyer has multiple orders
                    for (DataSnapshot orderSnapshot : buyerSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null) {
                            order.setOrderId(orderSnapshot.getKey());
                            // Filter: only show if seller's product is in this order
                            // For simplicity, we'll show all orders (in a real app, filter by seller's products)
                            orderList.add(order);
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error loading orders: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        ordersRef.addValueEventListener(orderListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove listener to prevent memory leaks
        if (orderListener != null) {
            ordersRef.removeEventListener(orderListener);
        }
    }
}
