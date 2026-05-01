package com.livisync.smd_a2.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.livisync.smd_a2.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing product data from Firebase Realtime Database.
 * Follows MVVM pattern by exposing LiveData for product lists.
 */
public class ProductRepository {
    private DatabaseReference productsRef;
    private MutableLiveData<List<Product>> allProducts;
    private MutableLiveData<List<Product>> sellerProducts;

    public ProductRepository() {
        productsRef = FirebaseDatabase.getInstance().getReference("products");
        allProducts = new MutableLiveData<>(new ArrayList<>());
        sellerProducts = new MutableLiveData<>(new ArrayList<>());
    }

    /**
     * Get MutableLiveData of all products
     * @return MutableLiveData containing list of all products
     */
    public MutableLiveData<List<Product>> getAllProducts() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Product> products = new ArrayList<>();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(productSnapshot.getKey());
                        products.add(product);
                    }
                }
                allProducts.setValue(products);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
        return allProducts;
    }

    /**
     * Get MutableLiveData of products for a specific seller
     * @param sellerId Seller's unique ID
     * @return MutableLiveData containing list of seller's products
     */
    public MutableLiveData<List<Product>> getSellerProducts(String sellerId) {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Product> products = new ArrayList<>();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null && sellerId.equals(product.getSellerId())) {
                        product.setProductId(productSnapshot.getKey());
                        products.add(product);
                    }
                }
                sellerProducts.setValue(products);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
        return sellerProducts;
    }

    /**
     * Add a new product to Firebase
     * @param product Product object to add
     * @return true if successful
     */
    public boolean addProduct(Product product) {
        try {
            String productId = productsRef.push().getKey();
            if (productId != null) {
                productsRef.child(productId).setValue(product);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
