package com.livisync.smd_a2.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> products;
    private final SharedPreferences prefs;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.prefs = context.getSharedPreferences("app.settings", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.tvDesc.setText(product.getDescription());
        holder.imgProduct.setImageResource(product.getImageResId());
        holder.tvProductId.setText(String.valueOf(position));

        // check if already in favourites
        boolean isFavourite = prefs.getBoolean("fav." + position, false);
        holder.imgHeart.setImageResource(isFavourite ?
                android.R.drawable.btn_star_big_on :
                android.R.drawable.btn_star_big_off);

        holder.imgHeart.setOnClickListener(v -> {
            boolean currentState = prefs.getBoolean("fav." + position, false);
            SharedPreferences.Editor editor = prefs.edit();

            if (currentState) {
                editor.putBoolean("fav." + position, false);
                editor.remove("fav.name." + position);
                editor.remove("fav.price." + position);
                editor.remove("fav.desc." + position);
                holder.imgHeart.setImageResource(android.R.drawable.btn_star_big_off);
            } else {
                editor.putBoolean("fav." + position, true);
                editor.putString("fav.name." + position, product.getName());
                editor.putString("fav.price." + position, String.valueOf(product.getPrice()));
                editor.putString("fav.desc." + position, product.getDescription());
                holder.imgHeart.setImageResource(android.R.drawable.btn_star_big_on);
            }
            editor.apply();
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgHeart;
        TextView tvName, tvPrice, tvDesc, tvProductId;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvDesc = itemView.findViewById(R.id.tvProductDescription);
            tvProductId = itemView.findViewById(R.id.tvProductId);
        }
    }
}
