package com.livisync.smd_a2.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.livisync.smd_a2.models.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing order data from Firebase Realtime Database.
 * Follows MVVM pattern by exposing LiveData for order lists.
 */
public class OrderRepository {
    private DatabaseReference ordersRef;
    private MutableLiveData<List<Order>> allOrders;
    private MutableLiveData<List<Order>> buyerOrders;

    public OrderRepository() {
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        allOrders = new MutableLiveData<>(new ArrayList<>());
        buyerOrders = new MutableLiveData<>(new ArrayList<>());
    }

    /**
     * Get MutableLiveData of all orders (for seller view)
     * @return MutableLiveData containing list of all orders
     */
    public MutableLiveData<List<Order>> getAllOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot buyerSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : buyerSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null) {
                            order.setOrderId(orderSnapshot.getKey());
                            orders.add(order);
                        }
                    }
                }
                allOrders.setValue(orders);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
        return allOrders;
    }

    /**
     * Get MutableLiveData of orders for a specific buyer
     * @param buyerId Buyer's unique ID
     * @return MutableLiveData containing list of buyer's orders
     */
    public MutableLiveData<List<Order>> getBuyerOrders(String buyerId) {
        ordersRef.child(buyerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        order.setOrderId(orderSnapshot.getKey());
                        orders.add(order);
                    }
                }
                buyerOrders.setValue(orders);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
        return buyerOrders;
    }

    /**
     * Save a new order to Firebase
     * @param buyerId Buyer's ID
     * @param order Order object to save
     * @return true if successful
     */
    public boolean saveOrder(String buyerId, Order order) {
        try {
            String orderId = ordersRef.child(buyerId).push().getKey();
            if (orderId != null) {
                ordersRef.child(buyerId).child(orderId).setValue(order);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
