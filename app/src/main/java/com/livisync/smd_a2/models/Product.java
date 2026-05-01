package com.livisync.smd_a2.models;

public class Product {
    private String productId, name, type, description, sellerId;
    private double price, originalPrice;
    private int imageResId;

    public Product() {}

    public Product(String productId, String name, String type, String description,
                   double price, int imageResId, String sellerId) {
        this.productId = productId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.price = price;
        this.originalPrice = price;
        this.imageResId = imageResId;
        this.sellerId = sellerId;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public int getImageResId() { return imageResId; }
    public String getSellerId() { return sellerId; }
}