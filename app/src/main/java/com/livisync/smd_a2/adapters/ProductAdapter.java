package com.livisync.smd_a2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.activities.ProductDescriptionActivity;
import com.livisync.smd_a2.db.DatabaseHelper;
import com.livisync.smd_a2.models.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private boolean isSellerMode;
    private DatabaseHelper dbHelper;

    public ProductAdapter(Context context, List<Product> productList, boolean isSellerMode) {
        this.context = context;
        this.productList = productList;
        this.isSellerMode = isSellerMode;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("$" + product.getPrice());

        // Set hardcoded image based on imageResId
        int imageRes = getImageRes(product.getImageResId());
        holder.ivProduct.setImageResource(imageRes);

        // Heart icon - favourites (buyer mode only)
        if (!isSellerMode) {
            boolean isFav = dbHelper.isFavourite(product.getProductId());
            holder.ivHeart.setImageResource(isFav ?
                    android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

            holder.ivHeart.setOnClickListener(v -> {
                if (dbHelper.isFavourite(product.getProductId())) {
                    dbHelper.removeFavourite(product.getProductId());
                    holder.ivHeart.setImageResource(android.R.drawable.btn_star_big_off);
                } else {
                    dbHelper.addFavourite(product);
                    holder.ivHeart.setImageResource(android.R.drawable.btn_star_big_on);
                }
            });
        } else {
            holder.ivHeart.setVisibility(View.GONE);
        }

        // Click to open product description
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDescriptionActivity.class);
            intent.putExtra("productId", product.getProductId());
            intent.putExtra("name", product.getName());
            intent.putExtra("type", product.getType());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("description", product.getDescription());
            intent.putExtra("imageResId", product.getImageResId());
            intent.putExtra("sellerId", product.getSellerId());
            context.startActivity(intent);
        });
    }

    private int getImageRes(int id) {
        // Use default Android drawables as placeholders
        int[] images = {
                android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery
        };
        if (id >= 0 && id < images.length) return images[id];
        return images[0];
    }

    @Override
    public int getItemCount() { return productList.size(); }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct, ivHeart;
        TextView tvName, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProductImage);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}