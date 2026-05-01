package com.livisync.smd_a2.models;

public class CartItem {
    private int id, imageResId, quantity;
    private String productId, name, type;
    private double price;

    public CartItem() {}

    public CartItem(String productId, String name, String type, double price, int imageResId, int quantity) {
        this.productId = productId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}