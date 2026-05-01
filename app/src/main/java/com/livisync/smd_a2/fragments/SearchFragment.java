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
        allProducts.add(new Product("1", "SONY WH-1000XM4 Black", "Electronics", "Model: WH-1000XM4, Black", 349.99, android.R.drawable.ic_menu_gallery, "seller1"));
        allProducts.add(new Product("2", "SONY WH-1000XM4 Beige", "Electronics", "Model: WH-1000XM4, Beige", 349.99, android.R.drawable.ic_menu_gallery, "seller1"));
        allProducts.add(new Product("3", "SHURE SM7B", "Electronics", "Studio microphone", 379.49, android.R.drawable.ic_menu_gallery, "seller2"));
        allProducts.add(new Product("4", "XIAOMI Redmi Watch 3", "Electronics", "42.58mm, Aluminium", 94.90, android.R.drawable.ic_menu_gallery, "seller3"));
        allProducts.add(new Product("5", "Google Nest Mini", "Electronics", "Google Assistant, IFTTT", 70.99, android.R.drawable.ic_menu_gallery, "seller4"));
        allProducts.add(new Product("6", "Samsung Galaxy Buds", "Electronics", "Active noise cancellation", 149.99, android.R.drawable.ic_menu_gallery, "seller5"));
        allProducts.add(new Product("7", "JBL Flip 6", "Electronics", "Portable waterproof speaker", 99.99, android.R.drawable.ic_menu_gallery, "seller6"));
        allProducts.add(new Product("8", "Apple Watch SE", "Electronics", "GPS, 40mm, Aluminium", 229.99, android.R.drawable.ic_menu_gallery, "seller7"));
        allProducts.add(new Product("9", "Logitech MX Master 3", "Electronics", "Advanced wireless mouse", 89.99, android.R.drawable.ic_menu_gallery, "seller8"));
        allProducts.add(new Product("10", "Keychron K2", "Electronics", "Wireless mechanical keyboard", 89.99, android.R.drawable.ic_menu_gallery, "seller9"));
        allProducts.add(new Product("11", "Dell 27 Monitor", "Electronics", "4K IPS display", 399.99, android.R.drawable.ic_menu_gallery, "seller10"));
        allProducts.add(new Product("12", "Anker PowerBank", "Electronics", "20000mAh, 65W", 59.99, android.R.drawable.ic_menu_gallery, "seller1"));
        allProducts.add(new Product("13", "GoPro Hero 12", "Electronics", "5.3K video, waterproof", 349.99, android.R.drawable.ic_menu_gallery, "seller2"));
        allProducts.add(new Product("14", "DJI Mini 3", "Electronics", "Lightweight drone, 4K", 459.99, android.R.drawable.ic_menu_gallery, "seller3"));
        allProducts.add(new Product("15", "Kindle Paperwhite", "Electronics", "6.8 display, waterproof", 139.99, android.R.drawable.ic_menu_gallery, "seller4"));
        allProducts.add(new Product("16", "Razer DeathAdder", "Electronics", "Gaming mouse, 20K DPI", 49.99, android.R.drawable.ic_menu_gallery, "seller5"));
        allProducts.add(new Product("17", "HyperX Cloud II", "Electronics", "Gaming headset, 7.1", 79.99, android.R.drawable.ic_menu_gallery, "seller6"));
        allProducts.add(new Product("18", "Elgato Stream Deck", "Electronics", "15 LCD keys", 129.99, android.R.drawable.ic_menu_gallery, "seller7"));
        allProducts.add(new Product("19", "Blue Yeti Mic", "Electronics", "USB condenser microphone", 109.99, android.R.drawable.ic_menu_gallery, "seller8"));
        allProducts.add(new Product("20", "TP-Link Deco XE75", "Electronics", "WiFi 6E mesh system", 179.99, android.R.drawable.ic_menu_gallery, "seller9"));
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
