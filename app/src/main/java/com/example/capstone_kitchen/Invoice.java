package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Invoice extends AppCompatActivity {

    private RecyclerView recyclerViewItems;
    private InvoiceOrderAdapter adapter;
    private FirebaseFirestore firestore;
    private String orderId;
    private String sapid;
    private double totalAmount;
    private TextView tvOrderNumber, tvPaymentAmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Initialize views
        tvOrderNumber = findViewById(R.id.tvOrdernumber);
        tvPaymentAmount = findViewById(R.id.tvPaymentamt);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);

        // Get intent data
        Intent intent = getIntent();
        sapid = intent.getStringExtra("sapid");
        orderId = intent.getStringExtra("orderId");
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0);

        // Set order info
        tvOrderNumber.setText("Order ID: " + orderId);
        tvPaymentAmount.setText("Total Amount: ₹" + totalAmount);

        // Firestore setup
        firestore = FirebaseFirestore.getInstance();

        // RecyclerView setup
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvoiceOrderAdapter(this, new ArrayList<>());
        recyclerViewItems.setAdapter(adapter);

        if (orderId != null) {
            fetchOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Order ID is missing", Toast.LENGTH_SHORT).show();
        }

        // ---------------- Setup Bottom Navigation ----------------
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottomnav_home) {
                Intent intent1 = new Intent(this, HomePage.class);
                intent1.putExtra("sapid", sapid);
                startActivity(intent1);
                return true;
            } else if (itemId == R.id.bottomnav_favorites) {
                Intent intent1 = new Intent(this, Favorites.class);
                intent1.putExtra("sapid", sapid);
                startActivity(intent1);
                return true;
            } else if (itemId == R.id.bottomnav_wallet) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("user").document(sapid).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Long walletPin = documentSnapshot.getLong("wallet_pin");
                                Intent intent1;
                                if (walletPin == null || walletPin == 0) {
                                    // No PIN set
                                    intent1 = new Intent(this, VirtualWallet.class);
                                } else {
                                    // PIN already set
                                    intent1 = new Intent(this, WalletDisplay.class);
                                }
                                intent1.putExtra("sapid", sapid);
                                startActivity(intent1);
                            } else {
                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to access wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                return true;
            } else if (itemId == R.id.bottomnav_cart) {
                if (orderId == null || orderId.isEmpty()) {
                    Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent1 = new Intent(this, Cart.class);
                    intent1.putExtra("sapid", sapid);
                    intent1.putExtra("orderId", orderId);
                    startActivity(intent1);
                }
                return true;
            }

            return false;
        });

    }

    private void fetchOrderDetails(String orderId) {
        DocumentReference orderRef = firestore.collection("order").document(orderId);

        orderRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> foodIds = (List<String>) documentSnapshot.get("food_id");
                List<Long> quantities = (List<Long>) documentSnapshot.get("quantity");
                List<Double> rates = (List<Double>) documentSnapshot.get("rate");
                List<Long> estTimes = (List<Long>) documentSnapshot.get("est_time");
                List<Long> counterIds = (List<Long>) documentSnapshot.get("counter_id");

                if (foodIds != null && quantities != null && rates != null && estTimes != null && counterIds != null) {
                    fetchFoodItems(foodIds, quantities, rates, estTimes, counterIds);
                } else {
                    Log.e("Invoice", "One or more fields are null in order document.");
                    Toast.makeText(Invoice.this, "Error: Missing data fields.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching order details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Invoice", "Error fetching order details: ", e);
        });
    }

    private void fetchFoodItems(List<String> foodIds, List<Long> quantities, List<Double> rates, List<Long> estTimes, List<Long> counterIds) {
        List<InvoiceOrderItem> orderItems = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);  // To track finished fetches

        for (int i = 0; i < foodIds.size(); i++) {
            final String foodId = foodIds.get(i);
            int finalI = i;

            firestore.collection("food_item").document(foodId).get()
                    .addOnSuccessListener(foodDoc -> {
                        if (foodDoc.exists()) {
                            String foodName = foodDoc.getString("food_name");
                            String foodImageUrl = foodDoc.getString("image");

                            final Long quantity = quantities.get(finalI);
                            final Double rate = rates.get(finalI);
                            final Long estTime = estTimes.get(finalI);
                            final Long counterId = counterIds.get(finalI);

                            if (foodName != null && foodImageUrl != null) {
                                InvoiceOrderItem orderItem = new InvoiceOrderItem(
                                        foodName,
                                        estTime.toString(),
                                        String.valueOf(counterId),
                                        rate,
                                        quantity.intValue(),
                                        foodImageUrl,
                                        quantity.intValue(),
                                        rate
                                );
                                orderItems.add(orderItem);
                            } else {
                                Log.e("Invoice", "Food data missing for ID: " + foodId);
                                Log.e("Invoice", "Data: " + foodDoc.getData());
                            }
                        } else {
                            Log.e("Invoice", "Food item not found: " + foodId);
                        }

                        // Update adapter after all food items fetched
                        if (counter.incrementAndGet() == foodIds.size()) {
                            adapter.updateOrderList(orderItems);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Invoice", "Error fetching food item: " + foodId + " - " + e.getMessage());
                        if (counter.incrementAndGet() == foodIds.size()) {
                            adapter.updateOrderList(orderItems);
                        }
                    });
        }
    }
}