package com.livisync.smd_a2.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.livisync.smd_a2.models.CartItem;
import com.livisync.smd_a2.repositories.CartRepository;

import java.util.List;

/**
 * ViewModel for CartFragment.
 * Manages cart data and total price with LiveData for UI observability.
 */
public class CartViewModel extends AndroidViewModel {
    private CartRepository cartRepository;
    private LiveData<List<CartItem>> cartItems;
    private LiveData<Double> totalPrice;

    public CartViewModel(Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        cartItems = cartRepository.getCartItems();
        totalPrice = cartRepository.getTotalPrice();
    }

    /**
     * Get LiveData of cart items
     * @return LiveData containing list of cart items
     */
    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    /**
     * Get LiveData of total cart price
     * @return LiveData containing total price
     */
    public LiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    /**
     * Refresh cart data
     */
    public void refreshCartData() {
        cartItems = cartRepository.getCartItems();
        totalPrice = cartRepository.getTotalPrice();
    }

    /**
     * Remove item from cart
     * @param productId Product ID to remove
     */
    public void removeFromCart(String productId) {
        cartRepository.removeFromCart(productId);
    }

    /**
     * Update quantity of item
     * @param productId Product ID
     * @param quantity New quantity
     */
    public void updateQuantity(String productId, int quantity) {
        cartRepository.updateQuantity(productId, quantity);
    }

    /**
     * Clear entire cart
     */
    public void clearCart() {
        cartRepository.clearCart();
    }
}
