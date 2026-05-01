package com.livisync.smd_a2.fragments.buyer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.adapters.FavouritesAdapter;
import com.livisync.smd_a2.db.CartDbHelper;
import com.livisync.smd_a2.db.FavouritesDbHelper;
import com.livisync.smd_a2.models.CartItem;
import com.livisync.smd_a2.models.Product;
import com.livisync.smd_a2.viewmodels.FavouritesViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying user's favourited products.
 * Fetches from SQLite database with real-time updates.
 */
public class FavouritesFragment extends Fragment {
    private RecyclerView rvFavourites;
    private FavouritesAdapter favouritesAdapter;
    private FavouritesDbHelper favouritesDbHelper;
    private CartDbHelper cartDbHelper;
    private FavouritesViewModel favouritesViewModel;
    private List<Product> favouritesList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        // Initialize database helpers
        favouritesDbHelper = new FavouritesDbHelper(requireContext());
        cartDbHelper = new CartDbHelper(requireContext());

        // Initialize ViewModel
        favouritesViewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);

        // Setup RecyclerView
        rvFavourites = view.findViewById(R.id.rvFavourites);
        favouritesList = new ArrayList<>();
        favouritesAdapter = new FavouritesAdapter(requireContext(), favouritesList, () -> {
            // Refresh on data changed
            favouritesViewModel.refreshFavourites();
        });

        rvFavourites.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavourites.setAdapter(favouritesAdapter);

        // Observe LiveData for favourites
        favouritesViewModel.getAllFavourites().observe(getViewLifecycleOwner(), favourites -> {
            if (favourites != null) {
                favouritesList.clear();
                favouritesList.addAll(favourites);
                favouritesAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    /**
     * Show confirmation dialog before deleting from favourites
     */
    private void showDeleteConfirmation(Product product, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove from Favourites")
                .setMessage("Do you want to delete " + product.getName() + " from favourites?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean success = favouritesDbHelper.removeFromFavourites(product.getProductId());
                    if (success) {
                        favouritesList.remove(position);
                        favouritesAdapter.notifyItemRemoved(position);
                        Toast.makeText(requireContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Add product to cart with quantity 1
     */
    private void addProductToCart(Product product) {
        CartItem item = new CartItem(product.getProductId(), product.getName(), product.getType(),
                product.getPrice(), product.getImageResId(), 1);
        boolean success = cartDbHelper.addToCart(item);
        if (success) {
            Toast.makeText(requireContext(), product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show();
        }
    }
}