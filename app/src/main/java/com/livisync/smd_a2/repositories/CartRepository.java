package com.livisync.smd_a2.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.livisync.smd_a2.db.CartDbHelper;
import com.livisync.smd_a2.models.CartItem;

import java.util.List;

/**
 * Repository for managing shopping cart data from SQLite database.
 * Follows MVVM pattern by exposing LiveData for cart items and total price.
 */
public class CartRepository {
    private CartDbHelper cartDbHelper;
    private MutableLiveData<List<CartItem>> cartItems;
    private MutableLiveData<Double> totalPrice;

    public CartRepository(Context context) {
        cartDbHelper = new CartDbHelper(context);
        cartItems = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>(0.0);
    }

    /**
     * Get MutableLiveData of all cart items
     * @return MutableLiveData containing list of cart items
     */
    public MutableLiveData<List<CartItem>> getCartItems() {
        List<CartItem> items = cartDbHelper.getAllCartItems();
        cartItems.setValue(items);
        return cartItems;
    }

    /**
     * Get MutableLiveData of total cart price
     * @return MutableLiveData containing total price
     */
    public MutableLiveData<Double> getTotalPrice() {
        double total = cartDbHelper.getTotalPrice();
        totalPrice.setValue(total);
        return totalPrice;
    }

    /**
     * Add item to cart
     * @param cartItem CartItem to add
     * @return true if successful
     */
    public boolean addToCart(CartItem cartItem) {
        boolean success = cartDbHelper.addToCart(cartItem);
        if (success) {
            refreshCartData();
        }
        return success;
    }

    /**
     * Update quantity of item in cart
     * @param productId Product ID
     * @param quantity New quantity
     * @return true if successful
     */
    public boolean updateQuantity(String productId, int quantity) {
        boolean success = cartDbHelper.updateQuantity(productId, quantity);
        if (success) {
            refreshCartData();
        }
        return success;
    }

    /**
     * Remove item from cart
     * @param productId Product ID to remove
     * @return true if successful
     */
    public boolean removeFromCart(String productId) {
        boolean success = cartDbHelper.removeFromCart(productId);
        if (success) {
            refreshCartData();
        }
        return success;
    }

    /**
     * Clear all cart items
     */
    public void clearCart() {
        cartDbHelper.clearCart();
        refreshCartData();
    }

    /**
     * Refresh cart data (called after modifications)
     */
    private void refreshCartData() {
        List<CartItem> items = cartDbHelper.getAllCartItems();
        cartItems.setValue(items);
        
        double total = cartDbHelper.getTotalPrice();
        totalPrice.setValue(total);
    }
}
