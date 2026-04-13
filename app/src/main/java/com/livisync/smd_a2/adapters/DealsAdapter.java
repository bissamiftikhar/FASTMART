package com.livisync.smd_a2.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.models.Product;

import java.util.List;

public class DealsAdapter extends ArrayAdapter<Product> {

    private final Context context;
    private final List<Product> deals;

    public DealsAdapter(Context context, List<Product> deals) {
        super(context, 0, deals);
        this.context = context;
        this.deals = deals;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_deal, parent, false);
        }

        Product product = deals.get(position);

        if (product != null) {
            TextView tvName = convertView.findViewById(R.id.tvDealName);
            TextView tvPrice = convertView.findViewById(R.id.tvDealPrice);
            TextView tvOriginal = convertView.findViewById(R.id.tvOriginalPrice);
            TextView tvDesc = convertView.findViewById(R.id.tvDealDescription);
            ImageView imgDeal = convertView.findViewById(R.id.imgDeal);

            tvName.setText(product.getName());
            tvPrice.setText(String.format("$%.2f", product.getPrice()));
            
            // Apply strikethrough in Java since android:paintFlags isn't a valid XML attribute
            tvOriginal.setText(String.format("$%.2f", product.getOriginalPrice()));
            tvOriginal.setPaintFlags(tvOriginal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            tvDesc.setText(product.getDescription());
            imgDeal.setImageResource(product.getImageResId());
        }

        return convertView;
    }
}
