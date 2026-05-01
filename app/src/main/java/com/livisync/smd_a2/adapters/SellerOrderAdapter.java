package com.livisync.smd_a2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.models.Order;

import java.util.List;

/**
 * Adapter for displaying seller's order history.
 * Shows order details with status badge.
 */
public class SellerOrderAdapter extends RecyclerView.Adapter<SellerOrderAdapter.ViewHolder> {
    private List<Order> orderList;

    public SellerOrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        holder.tvOrderId.setText("Order: " + order.getOrderId());
        holder.tvTimestamp.setText("Date: " + order.getTimestamp());
        holder.tvProductNames.setText("Products: " + order.getProductNames());
        holder.tvTotalPrice.setText("Total: $" + order.getTotalPrice());
        
        // Set status badge with color
        String status = order.getStatus();
        holder.tvStatus.setText("Status: " + status);
        
        if ("PROCESSING".equals(status)) {
            holder.tvStatus.setBackgroundColor(holder.itemView.getContext().getColor(R.color.status_processing));
        } else if ("IN TRANSIT".equals(status)) {
            holder.tvStatus.setBackgroundColor(holder.itemView.getContext().getColor(R.color.status_transit));
        } else if ("DELIVERED".equals(status)) {
            holder.tvStatus.setBackgroundColor(holder.itemView.getContext().getColor(R.color.status_delivered));
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvTimestamp, tvProductNames, tvTotalPrice, tvStatus;
        LinearLayout containerOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvTimestamp = itemView.findViewById(R.id.tv_order_date);
            tvProductNames = itemView.findViewById(R.id.tv_order_products);
            tvTotalPrice = itemView.findViewById(R.id.tv_order_total);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            containerOrder = itemView.findViewById(R.id.container_order);
        }
    }
}
