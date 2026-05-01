package com.livisync.smd_a2.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.utils.SessionManager;

/**
 * Activity for adding new products.
 * Allows seller to input product details and upload to Firebase Realtime Database.
 */
public class AddProductActivity extends AppCompatActivity {
    private EditText etProductName, etProductType, etProductPrice, etProductDescription, etImageUrl;
    private Spinner spImage;
    private Button btnAddProduct;
    private ProgressBar progressBar;
    private DatabaseReference productsRef;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Firebase and UI components
        productsRef = FirebaseDatabase.getInstance().getReference("products");
        sessionManager = new SessionManager(this);

        etProductName = findViewById(R.id.et_product_name);
        etProductType = findViewById(R.id.et_product_type);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductDescription = findViewById(R.id.et_product_description);
        etImageUrl = findViewById(R.id.et_image_url);
        spImage = findViewById(R.id.sp_image);
        btnAddProduct = findViewById(R.id.btn_add_product);
        progressBar = findViewById(R.id.progressBar);

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup image spinner
        setupImageSpinner();

        // Add product button click listener
        btnAddProduct.setOnClickListener(v -> addProduct());
    }

    /**
     * Setup spinner with predefined image options
     */
    private void setupImageSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.image_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spImage.setAdapter(adapter);
    }

    /**
     * Validate input and add product to Firebase
     */
    private void addProduct() {
        String productName = etProductName.getText().toString().trim();
        String productType = etProductType.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // Validation
        if (productName.isEmpty()) {
            etProductName.setError("Product name is required");
            return;
        }
        if (productType.isEmpty()) {
            etProductType.setError("Product type is required");
            return;
        }
        if (priceStr.isEmpty()) {
            etProductPrice.setError("Price is required");
            return;
        }
        if (description.isEmpty()) {
            etProductDescription.setError("Description is required");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etProductPrice.setError("Price must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            etProductPrice.setError("Invalid price format");
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);

        // Create product object
        ProductData productData = new ProductData(
                productName,
                productType,
                price,
                description,
                imageUrl,
                sessionManager.getUid()
        );

        // Generate new product ID and save to Firebase
        String productId = productsRef.push().getKey();
        if (productId != null) {
            productsRef.child(productId).setValue(productData)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddProductActivity.this, "Failed to add product: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Helper class for product data structure in Firebase
     */
    public static class ProductData {
        public String name, type, description, imageUrl, sellerId;
        public double price;

        public ProductData() {}

        public ProductData(String name, String type, double price, String description,
                          String imageUrl, String sellerId) {
            this.name = name;
            this.type = type;
            this.price = price;
            this.description = description;
            this.imageUrl = imageUrl;
            this.sellerId = sellerId;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
