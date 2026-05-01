package com.livisync.smd_a2.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.livisync.smd_a2.db.FavouritesDbHelper;
import com.livisync.smd_a2.models.Product;

import java.util.List;

/**
 * Repository for managing favourites data from SQLite database.
 * Follows MVVM pattern by exposing LiveData for favourites list.
 */
public class FavouritesRepository {
    private FavouritesDbHelper favouritesDbHelper;
    private MutableLiveData<List<Product>> favouritesList;

    public FavouritesRepository(Context context) {
        favouritesDbHelper = new FavouritesDbHelper(context);
        favouritesList = new MutableLiveData<>();
    }

    /**
     * Get MutableLiveData of all favourited products
     * @return MutableLiveData containing list of favourites
     */
    public MutableLiveData<List<Product>> getAllFavourites() {
        List<Product> favourites = favouritesDbHelper.getAllFavourites();
        favouritesList.setValue(favourites);
        return favouritesList;
    }

    /**
     * Add product to favourites
     * @param product Product to add
     * @return true if successful
     */
    public boolean addToFavourites(Product product) {
        boolean success = favouritesDbHelper.addToFavourites(product);
        if (success) {
            refreshFavourites();
        }
        return success;
    }

    /**
     * Remove product from favourites
     * @param productId Product ID to remove
     * @return true if successful
     */
    public boolean removeFromFavourites(String productId) {
        boolean success = favouritesDbHelper.removeFromFavourites(productId);
        if (success) {
            refreshFavourites();
        }
        return success;
    }

    /**
     * Check if product is favourited
     * @param productId Product ID to check
     * @return true if product is in favourites
     */
    public boolean isFavourited(String productId) {
        return favouritesDbHelper.isFavourited(productId);
    }

    /**
     * Clear all favourites
     */
    public void clearAllFavourites() {
        favouritesDbHelper.clearAllFavourites();
        refreshFavourites();
    }

    /**
     * Refresh favourites list
     */
    private void refreshFavourites() {
        List<Product> favourites = favouritesDbHelper.getAllFavourites();
        favouritesList.setValue(favourites);
    }
}
