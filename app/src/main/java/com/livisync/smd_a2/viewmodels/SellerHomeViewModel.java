package com.livisync.smd_a2.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.livisync.smd_a2.models.Product;
import com.livisync.smd_a2.repositories.ProductRepository;

import java.util.List;

/**
 * ViewModel for SellerHomeFragment.
 * Manages seller's product data and exposes LiveData for UI observability.
 */
public class SellerHomeViewModel extends ViewModel {
    private ProductRepository productRepository;
    private LiveData<List<Product>> sellerProducts;

    public SellerHomeViewModel() {
        productRepository = new ProductRepository();
    }

    /**
     * Load seller's products
     * @param sellerId Seller's unique ID
     * @return LiveData containing list of seller's products
     */
    public LiveData<List<Product>> loadSellerProducts(String sellerId) {
        sellerProducts = productRepository.getSellerProducts(sellerId);
        return sellerProducts;
    }

    /**
     * Get LiveData of seller's products
     * @return LiveData containing seller's products
     */
    public LiveData<List<Product>> getSellerProducts() {
        return sellerProducts;
    }
}
