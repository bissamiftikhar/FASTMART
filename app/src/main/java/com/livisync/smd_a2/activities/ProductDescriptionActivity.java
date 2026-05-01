package com.livisync.smd_a2.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.livisync.smd_a2.R;
import com.livisync.smd_a2.db.CartDbHelper;
import com.livisync.smd_a2.db.FavouritesDbHelper;
import com.livisync.smd_a2.models.CartItem;
import com.livisync.smd_a2.models.Product;

/**
 * Activity displaying detailed product information.
 * Allows user to add product to cart or toggle favourites.
 */
public class ProductDescriptionActivity extends AppCompatActivity {
    private CartDbHelper cartDbHelper;
    private FavouritesDbHelper favouritesDbHelper;
    private String productId;
    private Button btnBuyNow, btnFavourite;
    private boolean isFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        // Initialize database helpers
        cartDbHelper = new CartDbHelper(this);
        favouritesDbHelper = new FavouritesDbHelper(this);

        // Get product details from Intent
        productId = getIntent().getStringExtra("product_id");
        String name = getIntent().getStringExtra("product_name");
        String type = getIntent().getStringExtra("product_type");
        double price = getIntent().getDoubleExtra("product_price", 0);
        String description = getIntent().getStringExtra("product_description");
        int imageResId = getIntent().getIntExtra("product_image", 0);
        String sellerId = getIntent().getStringExtra("seller_id");

        // Setup UI components
        TextView tvName = findViewById(R.id.tv_prod_name);
        TextView tvType = findViewById(R.id.tv_prod_type);
        TextView tvPrice = findViewById(R.id.tv_prod_price);
        TextView tvDescription = findViewById(R.id.tv_prod_description);
        ImageView ivImage = findViewById(R.id.iv_prod_image);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        btnFavourite = findViewById(R.id.btn_favourite);

        // Set content
        tvName.setText(name != null ? name : "Product");
        tvType.setText(type != null ? type : "Type");
        tvPrice.setText("$" + price);
        tvDescription.setText(description != null ? description : "No description");

        // Set product image
        ivImage.setImageResource(imageResId);

        // Check if product is already in favourites
        isFavourite = favouritesDbHelper.isFavourited(productId);
        updateFavouriteButton();

        // Buy Now: Add to cart
        btnBuyNow.setOnClickListener(v -> {
            CartItem item = new CartItem(productId, name, type, price, imageResId, 1);
            boolean success = cartDbHelper.addToCart(item);
            if (success) {
                Toast.makeText(ProductDescriptionActivity.this, name + " added to cart!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProductDescriptionActivity.this, "Failed to add to cart",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Favourite toggle
        btnFavourite.setOnClickListener(v -> toggleFavourite(productId, name, type, description, price, imageResId, sellerId));

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Toggle favourite status for this product
     */
    private void toggleFavourite(String productId, String name, String type, String description,
                                 double price, int imageResId, String sellerId) {
        if (isFavourite) {
            // Remove from favourites
            boolean success = favouritesDbHelper.removeFromFavourites(productId);
            if (success) {
                isFavourite = false;
                updateFavouriteButton();
                Toast.makeText(this, "Removed from favourites", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Add to favourites
            Product product = new Product(productId, name, type, description, price, imageResId, sellerId);
            boolean success = favouritesDbHelper.addToFavourites(product);
            if (success) {
                isFavourite = true;
                updateFavouriteButton();
                Toast.makeText(this, "Added to favourites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Update favourite button appearance based on current status
     */
    private void updateFavouriteButton() {
        if (isFavourite) {
            btnFavourite.setText("❤ Favourited");
            btnFavourite.setBackgroundColor(getColor(R.color.heart_red));
        } else {
            btnFavourite.setText("♡ Add to Favourites");
            btnFavourite.setBackgroundColor(getColor(R.color.button_grey));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}