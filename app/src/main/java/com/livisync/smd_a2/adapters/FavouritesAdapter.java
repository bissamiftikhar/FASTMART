package com.livisync.smd_a2.adapters;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.db.DatabaseHelper;
import com.livisync.smd_a2.models.CartItem;
import com.livisync.smd_a2.models.Product;
import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavViewHolder> {

    private Context context;
    private List<Product> favList;
    private DatabaseHelper dbHelper;
    private Runnable onDataChanged;

    public FavouritesAdapter(Context context, List<Product> favList, Runnable onDataChanged) {
        this.context = context;
        this.favList = favList;
        this.dbHelper = new DatabaseHelper(context);
        this.onDataChanged = onDataChanged;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        Product p = favList.get(position);

        holder.tvName.setText(p.getName());
        holder.tvPrice.setText("$" + p.getPrice());

        int[] images = {android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery};
        int imgId = p.getImageResId();
        holder.ivImage.setImageResource(imgId >= 0 && imgId < images.length ? images[imgId] : images[0]);

        // Three dot menu - delete from favourites
        holder.ivMenu.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove Favourite")
                    .setMessage("Do you want to delete this product from favourites?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.removeFavourite(p.getProductId());
                        favList.remove(position);
                        notifyItemRemoved(position);
                        onDataChanged.run();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Cart icon - add to cart
        holder.ivCart.setOnClickListener(v -> {
            CartItem item = new CartItem(p.getProductId(), p.getName(),
                    p.getType(), p.getPrice(), p.getImageResId(), 1);
            dbHelper.addToCart(item);
            Toast.makeText(context, p.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() { return favList.size(); }

    public static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivMenu, ivCart;
        TextView tvName, tvPrice;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivFavImage);
            ivMenu = itemView.findViewById(R.id.ivFavMenu);
            ivCart = itemView.findViewById(R.id.ivFavCart);
            tvName = itemView.findViewById(R.id.tvFavName);
            tvPrice = itemView.findViewById(R.id.tvFavPrice);
        }
    }
}