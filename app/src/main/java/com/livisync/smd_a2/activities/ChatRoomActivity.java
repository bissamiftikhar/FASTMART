package com.livisync.smd_a2.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Activity displaying real-time chat room between buyer and seller.
 * Uses Firebase Realtime Database with ChildEventListener for live message updates.
 * Messages displayed as dynamically added TextViews with sender/receiver styling.
 */
public class ChatRoomActivity extends AppCompatActivity {
    private EditText etMessage;
    private Button btnSend;
    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private DatabaseReference chatRef;
    private SessionManager sessionManager;
    private String currentUserId, sellerId, chatId;
    private ChildEventListener messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // Initialize
        etMessage = findViewById(R.id.et_chat_message);
        btnSend = findViewById(R.id.btn_send_message);
        chatContainer = findViewById(R.id.chat_container);
        scrollView = findViewById(R.id.chat_scroll_view);

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUid();
        sellerId = getIntent().getStringExtra("seller_id");
        String sellerName = getIntent().getStringExtra("seller_name");

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(sellerName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Generate chat ID from sorted user IDs (ensures unique consistent ID)
        if (currentUserId.compareTo(sellerId) < 0) {
            chatId = currentUserId + "_" + sellerId;
        } else {
            chatId = sellerId + "_" + currentUserId;
        }

        // Reference to chat messages
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");

        // Listen for messages in real-time using ChildEventListener
        attachMessageListener();

        // Send button click listener
        btnSend.setOnClickListener(v -> sendMessage());
    }

    /**
     * Attach ChildEventListener to listen for real-time message updates
     */
    private void attachMessageListener() {
        messageListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                String senderId = snapshot.child("senderId").getValue(String.class);
                String receiverId = snapshot.child("receiverId").getValue(String.class);
                String text = snapshot.child("text").getValue(String.class);
                String timestamp = snapshot.child("timestamp").getValue(String.class);
                boolean isSender = currentUserId.equals(senderId);
                displayMessageBubble(text, timestamp, isSender);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatRoomActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        chatRef.addChildEventListener(messageListener);
    }

    /**
     * Send message to Firebase Realtime Database
     */
    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String messageId = chatRef.push().getKey();

        if (messageId != null) {
            Map<String, Object> message = new HashMap<>();
            message.put("senderId", currentUserId);
            message.put("receiverId", sellerId);
            message.put("text", messageText);
            message.put("timestamp", timestamp);

            chatRef.child(messageId).setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        etMessage.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ChatRoomActivity.this, "Failed to send message: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * Display message as a bubble TextView in the chat container.
     * Sender messages appear right-aligned with blue background.
     * Receiver messages appear left-aligned with grey background.
     * @param text Message text
     * @param timestamp Message timestamp
     * @param isSender True if current user sent the message
     */
    private void displayMessageBubble(String text, String timestamp, boolean isSender) {
        TextView messageBubble = new TextView(this);
        messageBubble.setText(text + "\n" + timestamp);
        messageBubble.setPadding(16, 12, 16, 12);
        messageBubble.setTextSize(14);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);

        if (isSender) {
            // Sender: right-aligned, blue background
            messageBubble.setBackgroundResource(R.drawable.bubble_sent);
            messageBubble.setTextColor(getColor(R.color.white));
            params.gravity = android.view.Gravity.END;
            params.leftMargin = 80;
        } else {
            // Receiver: left-aligned, grey background
            messageBubble.setBackgroundResource(R.drawable.bubble_received);
            messageBubble.setTextColor(getColor(R.color.text_primary_light));
            params.gravity = android.view.Gravity.START;
            params.rightMargin = 80;
        }

        messageBubble.setLayoutParams(params);
        chatContainer.addView(messageBubble);

        // Auto-scroll to bottom
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove listener to prevent memory leaks
        if (messageListener != null) {
            chatRef.removeEventListener(messageListener);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
