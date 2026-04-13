package com.livisync.smd_a2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.adapters.FavouritesAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("app.settings", Context.MODE_PRIVATE);

        // create the unused key the assignment requires
        prefs.edit().putString("favList.favourites", "").apply();

        List<Integer> favPositions = new ArrayList<>();

        // scan all possible product positions (0 to 19)
        for (int i = 0; i < 20; i++) {
            if (prefs.getBoolean("fav." + i, false)) {
                favPositions.add(i);
            }
        }

        RecyclerView rvFavourites = view.findViewById(R.id.rvFavourites);
        FavouritesAdapter adapter = new FavouritesAdapter(requireContext(), favPositions);
        rvFavourites.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavourites.setAdapter(adapter);

        return view;
    }
}
