package com.example.capstone_kitchen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WalletHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewWalletHistory;
    private WalletHistoryAdapter adapter;
    private List<WalletTransactionModel> transactionList;
    private static final String TAG = "WalletHistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_history);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        ivBack.setOnClickListener(v -> startActivity(new Intent(WalletHistoryActivity.this, SideNavigation.class)));
        ivMenu.setOnClickListener(v -> startActivity(new Intent(WalletHistoryActivity.this, SideNavigation.class)));

        recyclerViewWalletHistory = findViewById(R.id.recyclerViewWalletHistory);
        recyclerViewWalletHistory.setLayoutManager(new LinearLayoutManager(this));

        transactionList = new ArrayList<>();
        adapter = new WalletHistoryAdapter(transactionList);
        recyclerViewWalletHistory.setAdapter(adapter);

        // Get SAP ID from intent
        Intent intent = getIntent();
        String sapid = intent.getStringExtra("sapid");
        Log.d(TAG, "Received SAP ID: " + sapid);

        // Construct the user path as /user/{sapid}
        String userPath = "/user/" + sapid;
        Log.d(TAG, "User path for Firestore query: " + userPath);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get reference to the user document
        DocumentReference userRef = db.document(userPath);

        // Query Firestore to match user_id with the reference of /user/{sapid}
        db.collection("payment")
                .whereEqualTo("user_id", userRef)  // Use the reference to query user_id
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "No transactions found for user: " + sapid);
                    } else {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            // Handle order_id as a reference
                            DocumentReference orderRef = doc.getDocumentReference("order_id");
                            String orderId = orderRef != null ? orderRef.getId() : "Credit";  // Extract order ID from reference

                            String mode = doc.getString("mode");
                            String paymentStatus = doc.getString("payment_status");
                            double amount = doc.getDouble("amount");

                            // Get timestamp and format it
                            Date date = doc.getDate("date");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy | hh:mm a");
                            String formattedDate = dateFormat.format(date);

                            Log.d(TAG, "Transaction fetched: order_id=" + orderId + ", mode=" + mode + ", amount=" + amount + ", date=" + formattedDate);

                            // Add transaction to list
                            WalletTransactionModel transaction = new WalletTransactionModel(
                                    orderId, formattedDate, amount, paymentStatus
                            );
                            transactionList.add(transaction);
                        }
                        // Notify adapter that data has changed
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch wallet history", e);
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottomnav_home) {
                startActivity(new Intent(WalletHistoryActivity.this, HomePage.class));
                return true;
            } else if (itemId == R.id.bottomnav_favorites) {
                startActivity(new Intent(WalletHistoryActivity.this, Favorites.class));
                return true;
            } else if (itemId == R.id.bottomnav_wallet) {
                startActivity(new Intent(WalletHistoryActivity.this, VirtualWallet.class));
                return true;
            } else if (itemId == R.id.bottomnav_cart) {
                startActivity(new Intent(WalletHistoryActivity.this, Cart.class));
                return true;
            }
            return false;
        });
    }
}