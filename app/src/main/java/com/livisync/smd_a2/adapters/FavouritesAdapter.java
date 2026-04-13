package com.livisync.smd_a2.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.livisync.smd_a2.R;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavViewHolder> {

    private final Context context;
    private final List<Integer> favPositions;
    private final SharedPreferences prefs;

    public FavouritesAdapter(Context context, List<Integer> favPositions) {
        this.context = context;
        this.favPositions = favPositions;
        this.prefs = context.getSharedPreferences("app.settings", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        int productIndex = favPositions.get(position);

        String name = prefs.getString("fav.name." + productIndex, "");
        String price = prefs.getString("fav.price." + productIndex, "");
        String desc = prefs.getString("fav.desc." + productIndex, "");

        holder.tvName.setText(name);
        holder.tvPrice.setText(String.format("$%s", price));
        holder.tvDesc.setText(desc);

        holder.btnMoreOptions.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove Favourite")
                    .setMessage("Are you sure you want to delete this from favourites?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("fav." + productIndex);
                        editor.remove("fav.name." + productIndex);
                        editor.remove("fav.price." + productIndex);
                        editor.remove("fav.desc." + productIndex);
                        editor.apply();

                        int currentPosition = holder.getAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            favPositions.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            notifyItemRangeChanged(currentPosition, favPositions.size());
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.btnAddToCart.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("cart.name." + productIndex, name);
            editor.putString("cart.price." + productIndex, price);
            editor.putString("cart.desc." + productIndex, desc);
            editor.putInt("cart.qty." + productIndex, 1);
            editor.putBoolean("cart." + productIndex, true);
            editor.apply();

            Toast.makeText(context, name + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return favPositions.size();
    }

    public static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnAddToCart, btnMoreOptions;
        TextView tvName, tvPrice, tvDesc;

        public FavViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgFavProduct);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);
            tvName = itemView.findViewById(R.id.tvFavName);
            tvPrice = itemView.findViewById(R.id.tvFavPrice);
            tvDesc = itemView.findViewById(R.id.tvFavDesc);
        }
    }
}
