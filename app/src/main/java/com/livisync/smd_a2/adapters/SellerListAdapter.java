package com.livisync.smd_a2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.models.User;

import java.util.List;

/**
 * Adapter for displaying list of sellers in chat selection.
 */
public class SellerListAdapter extends RecyclerView.Adapter<SellerListAdapter.ViewHolder> {
    private List<User> sellerList;
    private OnSellerClickListener listener;

    public interface OnSellerClickListener {
        void onSellerClick(User seller);
    }

    public SellerListAdapter(List<User> sellerList, OnSellerClickListener listener) {
        this.sellerList = sellerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User seller = sellerList.get(position);
        holder.tvSellerName.setText(seller.getName());
        holder.tvSellerEmail.setText(seller.getEmail());
        holder.itemView.setOnClickListener(v -> listener.onSellerClick(seller));
    }

    @Override
    public int getItemCount() {
        return sellerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSellerName, tvSellerEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSellerName = itemView.findViewById(R.id.tv_seller_name);
            tvSellerEmail = itemView.findViewById(R.id.tv_seller_email);
        }
    }
}
