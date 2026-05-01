package com.livisync.smd_a2.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.livisync.smd_a2.models.Product;
import com.livisync.smd_a2.repositories.ProductRepository;

import java.util.List;

/**
 * ViewModel for HomeFragment.
 * Manages product data and exposes LiveData for UI observability.
 */
public class HomeViewModel extends ViewModel {
    private ProductRepository productRepository;
    private LiveData<List<Product>> allProducts;

    public HomeViewModel() {
        productRepository = new ProductRepository();
        allProducts = productRepository.getAllProducts();
    }

    /**
     * Get LiveData of all products
     * @return LiveData containing list of products
     */
    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }
}
