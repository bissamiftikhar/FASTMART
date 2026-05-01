package com.livisync.smd_a2.fragments.buyer;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.adapters.ProductAdapter;
import com.livisync.smd_a2.models.Product;
import com.livisync.smd_a2.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private DatabaseReference productsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SessionManager session = new SessionManager(requireContext());
        TextView tvHello = view.findViewById(R.id.tvHelloUser);
        tvHello.setText("Hello " + session.getName());

        rvProducts = view.findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductAdapter(requireContext(), productList, false);
        rvProducts.setAdapter(adapter);

        // Real-time Firebase listener
        productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product p = ds.getValue(Product.class);
                    if (p != null) {
                        p.setProductId(ds.getKey());
                        productList.add(p);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return view;
    }
}