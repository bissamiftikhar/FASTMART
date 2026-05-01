package com.livisync.smd_a2.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.livisync.smd_a2.models.Product;
import com.livisync.smd_a2.repositories.FavouritesRepository;

import java.util.List;

/**
 * ViewModel for FavouritesFragment.
 * Manages favourites data and exposes LiveData for UI observability.
 */
public class FavouritesViewModel extends AndroidViewModel {
    private FavouritesRepository favouritesRepository;
    private LiveData<List<Product>> favouritesList;

    public FavouritesViewModel(Application application) {
        super(application);
        favouritesRepository = new FavouritesRepository(application);
        favouritesList = favouritesRepository.getAllFavourites();
    }

    /**
     * Get LiveData of all favourites
     * @return LiveData containing list of favourited products
     */
    public LiveData<List<Product>> getAllFavourites() {
        return favouritesList;
    }

    /**
     * Remove product from favourites
     * @param productId Product ID to remove
     */
    public void removeFromFavourites(String productId) {
        favouritesRepository.removeFromFavourites(productId);
    }

    /**
     * Refresh favourites list
     */
    public void refreshFavourites() {
        favouritesList = favouritesRepository.getAllFavourites();
    }
}
