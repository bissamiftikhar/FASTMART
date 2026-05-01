package com.livisync.smd_a2.adapters;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.db.DatabaseHelper;
import com.livisync.smd_a2.models.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartList;
    private DatabaseHelper dbHelper;
    private Runnable onTotalChanged;

    public CartAdapter(Context context, List<CartItem> cartList, Runnable onTotalChanged) {
        this.context = context;
        this.cartList = cartList;
        this.dbHelper = new DatabaseHelper(context);
        this.onTotalChanged = onTotalChanged;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("$" + String.format("%.2f", item.getPrice() * item.getQuantity()));
        holder.tvQty.setText(String.valueOf(item.getQuantity()));

        int[] images = {android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery};
        int imgId = item.getImageResId();
        holder.ivImage.setImageResource(imgId >= 0 && imgId < images.length ? images[imgId] : images[0]);

        // Increase quantity
        holder.btnPlus.setOnClickListener(v -> {
            int qty = item.getQuantity() + 1;
            item.setQuantity(qty);
            dbHelper.updateCartQuantity(item.getProductId(), qty);
            holder.tvQty.setText(String.valueOf(qty));
            holder.tvPrice.setText("$" + String.format("%.2f", item.getPrice() * qty));
            onTotalChanged.run();
        });

        // Decrease quantity
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int qty = item.getQuantity() - 1;
                item.setQuantity(qty);
                dbHelper.updateCartQuantity(item.getProductId(), qty);
                holder.tvQty.setText(String.valueOf(qty));
                holder.tvPrice.setText("$" + String.format("%.2f", item.getPrice() * qty));
                onTotalChanged.run();
            }
        });

        // Three dot delete
        holder.ivMenu.setOnClickListener(v -> {
            dbHelper.removeFromCart(item.getProductId());
            cartList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size());
            onTotalChanged.run();
        });
    }

    @Override
    public int getItemCount() { return cartList.size(); }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivMenu;
        TextView tvName, tvPrice, tvQty;
        Button btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCartImage);
            ivMenu = itemView.findViewById(R.id.ivCartMenu);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvCartQty);
            btnPlus = itemView.findViewById(R.id.btnCartPlus);
            btnMinus = itemView.findViewById(R.id.btnCartMinus);
        }
    }
}