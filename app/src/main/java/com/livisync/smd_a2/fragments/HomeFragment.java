package com.livisync.smd_a2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.adapters.DealsAdapter;
import com.livisync.smd_a2.adapters.ProductAdapter;
import com.livisync.smd_a2.models.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // greeting
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        SharedPreferences prefs = requireActivity().getSharedPreferences("app.settings", Context.MODE_PRIVATE);
        String email = prefs.getString("user.email", "User");
        String name = (email != null) ? email.split("@")[0] : "User";
        tvGreeting.setText(String.format("Hello %s!", name));

        // build deals list
        List<Product> deals = new ArrayList<>();
        deals.add(new Product("RØDE PodMic", "Dynamic microphone, Speaker microphone", 108.20, 199.99, android.R.drawable.ic_menu_gallery));
        deals.add(new Product("SONY WH-1000XM4", "Noise cancelling headphones", 279.99, 349.99, android.R.drawable.ic_menu_gallery));
        deals.add(new Product("Apple AirPods Pro", "Active noise cancellation", 189.99, 249.99, android.R.drawable.ic_menu_gallery));

        // setup ListView horizontally
        ListView lvDeals = view.findViewById(R.id.lvDeals);
        DealsAdapter dealsAdapter = new DealsAdapter(requireContext(), deals);
        lvDeals.setAdapter(dealsAdapter);

        // build products list
        List<Product> products = new ArrayList<>();
        products.add(new Product("SONY WH-1000XM4 Black", "Model: WH-1000XM4, Black", 349.99, 399.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("SONY WH-1000XM4 Beige", "Model: WH-1000XM4, Beige", 349.99, 399.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("SHURE SM7B", "Studio microphone", 379.49, 429.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("XIAOMI Redmi Watch 3", "42.58mm, Aluminium, Plastic", 94.90, 119.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Google Nest Mini", "Google Assistant, IFTTT", 70.99, 99.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Samsung Galaxy Buds", "Active noise cancellation", 149.99, 199.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("JBL Flip 6", "Portable waterproof speaker", 99.99, 129.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Apple Watch SE", "GPS, 40mm, Aluminium", 229.99, 279.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Logitech MX Master 3", "Advanced wireless mouse", 89.99, 109.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Keychron K2", "Wireless mechanical keyboard", 89.99, 109.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Dell 27\" Monitor", "4K IPS display", 399.99, 499.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Anker PowerBank", "20000mAh, 65W fast charge", 59.99, 79.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("GoPro Hero 12", "5.3K video, waterproof", 349.99, 399.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("DJI Mini 3", "Lightweight drone, 4K", 459.99, 529.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Kindle Paperwhite", "6.8\" display, waterproof", 139.99, 169.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Razer DeathAdder", "Gaming mouse, 20K DPI", 49.99, 69.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("HyperX Cloud II", "Gaming headset, 7.1", 79.99, 99.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Elgato Stream Deck", "15 LCD keys", 129.99, 159.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("Blue Yeti Mic", "USB condenser microphone", 109.99, 139.99, android.R.drawable.ic_menu_gallery));
        products.add(new Product("TP-Link Deco XE75", "WiFi 6E mesh system", 179.99, 229.99, android.R.drawable.ic_menu_gallery));

        // setup RecyclerView
        RecyclerView rvProducts = view.findViewById(R.id.rvProducts);
        ProductAdapter productAdapter = new ProductAdapter(requireContext(), products);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        rvProducts.setLayoutManager(gridLayoutManager);
        rvProducts.setAdapter(productAdapter);

        return view;
    }
}
