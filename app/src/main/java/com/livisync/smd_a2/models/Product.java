package com.livisync.smd_a2.models;

public class Product {
    private String name;
    private String description;
    private double price;
    private double originalPrice;
    private int imageResId;

    public Product(String name, String description, double price, double originalPrice, int imageResId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public int getImageResId() { return imageResId; }
}
