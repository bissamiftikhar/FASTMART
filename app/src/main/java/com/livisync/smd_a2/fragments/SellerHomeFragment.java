package com.livisync.smd_a2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.activities.ProductDescriptionActivity;
import com.livisync.smd_a2.adapters.ProductAdapter;
import com.livisync.smd_a2.models.Product;
import com.livisync.smd_a2.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying seller's own products in a grid layout.
 * Fetches products from Firebase filtered by sellerId.
 */
public class SellerHomeFragment extends Fragment {
    private RecyclerView rvSellerProducts;
    private ProductAdapter productAdapter;
    private List<Product> sellerProducts;
    private DatabaseReference productsRef;
    private SessionManager sessionManager;
    private String currentSellerId;
    private ValueEventListener productListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_home, container, false);

        // Initialize
        rvSellerProducts = view.findViewById(R.id.rv_seller_products);
        productsRef = FirebaseDatabase.getInstance().getReference("products");
        sessionManager = new SessionManager(requireContext());
        currentSellerId = sessionManager.getUid();

        // Setup RecyclerView
        sellerProducts = new ArrayList<>();
        productAdapter = new ProductAdapter(requireContext(), sellerProducts, true);

        rvSellerProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvSellerProducts.setAdapter(productAdapter);

        // Fetch seller's products
        fetchSellerProducts();

        return view;
    }

    /**
     * Fetch products from Firebase filtered by current seller's ID using ValueEventListener
     */
    private void fetchSellerProducts() {
        productListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sellerProducts.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null && currentSellerId.equals(product.getSellerId())) {
                        product.setProductId(productSnapshot.getKey());
                        sellerProducts.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error loading products: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        productsRef.addValueEventListener(productListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove listener to prevent memory leaks
        if (productListener != null) {
            productsRef.removeEventListener(productListener);
        }
    }
}
