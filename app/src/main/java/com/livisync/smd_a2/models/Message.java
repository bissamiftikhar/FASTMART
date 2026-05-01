package com.livisync.smd_a2.models;

public class Message {
    private String senderId, receiverId, text, timestamp;

    public Message() {}

    public Message(String senderId, String receiverId, String text, String timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getText() { return text; }
    public String getTimestamp() { return timestamp; }
}