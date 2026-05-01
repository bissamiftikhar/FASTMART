package com.livisync.smd_a2.models;

public class Order {
    private String orderId, buyerId, productNames, timestamp, status;
    private double totalPrice;

    public Order() {}

    public Order(String orderId, String buyerId, String productNames,
                 double totalPrice, String timestamp, String status) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.productNames = productNames;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getBuyerId() { return buyerId; }
    public String getProductNames() { return productNames; }
    public double getTotalPrice() { return totalPrice; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}