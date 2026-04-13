package com.livisync.smd_a2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.models.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String SearchHashKey = "search.history";

    private EditText etSearch;
    private ListView lvRecentSearches;
    private TextView tvClearAll;
    private ImageView btnBack;
    private SharedPreferences prefs;
    private List<String> searchHistory;
    private ArrayAdapter<String> historyAdapter;

    private List<Product> allProducts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        prefs = requireActivity().getSharedPreferences("app.settings", Context.MODE_PRIVATE);

        etSearch = view.findViewById(R.id.etSearch);
        lvRecentSearches = view.findViewById(R.id.lvRecentSearches);
        tvClearAll = view.findViewById(R.id.tvClearAll);
        btnBack = view.findViewById(R.id.btnBack);

        buildProductList();
        loadSearchHistory();

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        tvClearAll.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(SearchHashKey);
            editor.apply();
            searchHistory.clear();
            historyAdapter.notifyDataSetChanged();
        });

        btnBack.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) requireActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            }
        });

        return view;
    }

    private void buildProductList() {
        allProducts = new ArrayList<>();
        allProducts.add(new Product("SONY WH-1000XM4 Black", "Model: WH-1000XM4, Black", 349.99, 399.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("SONY WH-1000XM4 Beige", "Model: WH-1000XM4, Beige", 349.99, 399.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("SHURE SM7B", "Studio microphone", 379.49, 429.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("XIAOMI Redmi Watch 3", "42.58mm, Aluminium", 94.90, 119.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Google Nest Mini", "Google Assistant, IFTTT", 70.99, 99.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Samsung Galaxy Buds", "Active noise cancellation", 149.99, 199.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("JBL Flip 6", "Portable waterproof speaker", 99.99, 129.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Apple Watch SE", "GPS, 40mm, Aluminium", 229.99, 279.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Logitech MX Master 3", "Advanced wireless mouse", 89.99, 109.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Keychron K2", "Wireless mechanical keyboard", 89.99, 109.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Dell 27 Monitor", "4K IPS display", 399.99, 499.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Anker PowerBank", "20000mAh, 65W", 59.99, 79.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("GoPro Hero 12", "5.3K video, waterproof", 349.99, 399.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("DJI Mini 3", "Lightweight drone, 4K", 459.99, 529.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Kindle Paperwhite", "6.8 display, waterproof", 139.99, 169.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Razer DeathAdder", "Gaming mouse, 20K DPI", 49.99, 69.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("HyperX Cloud II", "Gaming headset, 7.1", 79.99, 99.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Elgato Stream Deck", "15 LCD keys", 129.99, 159.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("Blue Yeti Mic", "USB condenser microphone", 109.99, 139.99, android.R.drawable.ic_menu_gallery));
        allProducts.add(new Product("TP-Link Deco XE75", "WiFi 6E mesh system", 179.99, 229.99, android.R.drawable.ic_menu_gallery));
    }

    private void loadSearchHistory() {
        String savedHistory = prefs.getString(SearchHashKey, "");
        searchHistory = new ArrayList<>();

        if (!savedHistory.isEmpty()) {
            String[] items = savedHistory.split(",");
            for (String item : items) {
                if (!item.trim().isEmpty()) {
                    searchHistory.add(item.trim());
                }
            }
        }

        historyAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, searchHistory);
        lvRecentSearches.setAdapter(historyAdapter);
    }

    private void saveSearchHistory() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < searchHistory.size(); i++) {
            sb.append(searchHistory.get(i));
            if (i < searchHistory.size() - 1) {
                sb.append(",");
            }
        }
        prefs.edit().putString(SearchHashKey, sb.toString()).apply();
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();

        if (query.isEmpty()) {
            etSearch.setError("Enter a search term");
            return;
        }

        if (!searchHistory.contains(query)) {
            searchHistory.add(0, query);
            historyAdapter.notifyDataSetChanged();
            saveSearchHistory();
        }

        boolean found = false;
        for (Product p : allProducts) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                found = true;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(found ? "Product Found" : "Not Found")
                .setMessage(found ? "Product is available in our store!" : "No product matched your search.")
                .setPositiveButton("OK", null)
                .show();
    }
}
