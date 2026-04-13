package com.livisync.smd_a2.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.livisync.smd_a2.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<Integer> cartPositions;
    private final List<Integer> quantities;
    private final SharedPreferences prefs;
    private final TotalUpdateListener totalUpdateListener;

    public interface TotalUpdateListener {
        void onTotalUpdated();
    }

    public CartAdapter(Context context, List<Integer> cartPositions,
                       List<Integer> quantities, TotalUpdateListener listener) {
        this.context = context;
        this.cartPositions = cartPositions;
        this.quantities = quantities;
        this.prefs = context.getSharedPreferences("app.settings", Context.MODE_PRIVATE);
        this.totalUpdateListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        int productIndex = cartPositions.get(position);
        int qty = quantities.get(position);

        String name = prefs.getString("cart.name." + productIndex, "");
        String price = prefs.getString("cart.price." + productIndex, "");
        String desc = prefs.getString("cart.desc." + productIndex, "");

        holder.tvName.setText(name);
        holder.tvPrice.setText(String.format("$%s", price));
        holder.tvDesc.setText(desc);
        holder.tvQuantity.setText(String.valueOf(qty));

        holder.btnIncrease.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                int currentQty = quantities.get(currentPos);
                quantities.set(currentPos, currentQty + 1);
                holder.tvQuantity.setText(String.valueOf(currentQty + 1));
                totalUpdateListener.onTotalUpdated();
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                int currentQty = quantities.get(currentPos);
                if (currentQty > 1) {
                    quantities.set(currentPos, currentQty - 1);
                    holder.tvQuantity.setText(String.valueOf(currentQty - 1));
                    totalUpdateListener.onTotalUpdated();
                }
            }
        });

        holder.btnMore.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                int prodIndex = cartPositions.get(currentPos);

                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("cart." + prodIndex);
                editor.remove("cart.name." + prodIndex);
                editor.remove("cart.price." + prodIndex);
                editor.remove("cart.desc." + prodIndex);
                editor.remove("cart.qty." + prodIndex);
                editor.apply();

                cartPositions.remove(currentPos);
                quantities.remove(currentPos);
                notifyItemRemoved(currentPos);
                notifyItemRangeChanged(currentPos, cartPositions.size());
                totalUpdateListener.onTotalUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartPositions.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView btnMore;
        TextView tvName, tvPrice, tvDesc, tvQuantity;
        Button btnIncrease, btnDecrease;

        public CartViewHolder(View itemView) {
            super(itemView);
            btnMore = itemView.findViewById(R.id.btnCartMore);
            tvName = itemView.findViewById(R.id.tvCartName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvDesc = itemView.findViewById(R.id.tvCartDesc);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}
