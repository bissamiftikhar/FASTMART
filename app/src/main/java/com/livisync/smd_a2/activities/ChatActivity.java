package com.livisync.smd_a2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.livisync.smd_a2.R;
import com.livisync.smd_a2.adapters.SellerListAdapter;
import com.livisync.smd_a2.models.User;
import com.livisync.smd_a2.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity displaying list of sellers for buyer to initiate chat.
 * Shows all users with accountType = "Seller" fetched from Firebase.
 */
public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvSellerList;
    private List<User> sellerList;
    private DatabaseReference usersRef;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private String currentBuyerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize
        rvSellerList = findViewById(R.id.rv_seller_list);
        progressBar = findViewById(R.id.progressBar);
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        sessionManager = new SessionManager(this);
        currentBuyerId = sessionManager.getUid();

        // Setup RecyclerView
        sellerList = new ArrayList<>();

        rvSellerList.setLayoutManager(new LinearLayoutManager(this));

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Fetch sellers
        fetchSellers();
    }

    /**
     * Fetch all sellers from Firebase Realtime Database
     */
    private void fetchSellers() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressBar.setVisibility(android.view.View.GONE);
                sellerList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        String accountType = userSnapshot.child("accountType").getValue(String.class);
                        if ("Seller".equals(accountType)) {
                            user.setUid(userSnapshot.getKey());
                            sellerList.add(user);
                        }
                    }
                }

                // Setup adapter after fetching sellers
                SellerListAdapter sellerAdapter = new SellerListAdapter(sellerList, seller -> {
                    // Click listener: open ChatRoomActivity
                    Intent intent = new Intent(ChatActivity.this, ChatRoomActivity.class);
                    intent.putExtra("seller_id", seller.getUid());
                    intent.putExtra("seller_name", seller.getName());
                    startActivity(intent);
                });
                rvSellerList.setAdapter(sellerAdapter);

                if (sellerList.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "No sellers available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(android.view.View.GONE);
                Toast.makeText(ChatActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
