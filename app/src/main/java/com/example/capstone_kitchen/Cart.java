package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    Button payButton;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private FirebaseFirestore db;
    private String currentOrderId;
    private String sapid;

    private TextView cartSubtotalTv;
    private TextView cartTotalTv;

    // Track total to pass to payment
    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sapid = getIntent().getStringExtra("sapid");
        String orderId = getIntent().getStringExtra("orderId");

        db = FirebaseFirestore.getInstance();

        payButton = findViewById(R.id.payButton);
        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems);
        recyclerView.setAdapter(cartAdapter);

        cartSubtotalTv = findViewById(R.id.cartsubtotaltv);
        cartTotalTv = findViewById(R.id.carttotaltv);

        fetchCartData(orderId);

        payButton.setOnClickListener(v -> {
            if (totalAmount > 0) {
                Intent intent = new Intent(Cart.this, Payment.class);
                intent.putExtra("sapid", sapid);
                intent.putExtra("orderId", currentOrderId);
                intent.putExtra("totalAmount", totalAmount);
                startActivity(intent);
            } else {
                Toast.makeText(Cart.this, "Total must be greater than 0 to proceed with payment.", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Cart.this, HomePage.class);
            intent.putExtra("sapid", sapid);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottomnav_home) {
                Intent intent = new Intent(this, HomePage.class);
                intent.putExtra("sapid", sapid);
                startActivity(intent);
            } else if (itemId == R.id.bottomnav_favorites) {
                Intent intent = new Intent(this, Favorites.class);
                intent.putExtra("sapid", sapid);
                startActivity(intent);
            } else if (itemId == R.id.bottomnav_wallet) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("user").document(sapid).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Long walletPin = documentSnapshot.getLong("wallet_pin");
                                Intent intent;
                                if (walletPin == null || walletPin == 0) {
                                    // No PIN set
                                    intent = new Intent(this, VirtualWallet.class);
                                } else {
                                    // PIN already set
                                    intent = new Intent(this, WalletDisplay.class);
                                }
                                intent.putExtra("sapid", sapid);
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to access wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                return true;
            } else if (itemId == R.id.bottomnav_cart) {
                Intent intent = new Intent(this, Cart.class);
                intent.putExtra("sapid", sapid);
                startActivity(intent);
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String orderId = getIntent().getStringExtra("orderId");
        fetchCartData(orderId);
    }

    private void fetchCartData(String orderId) {
        CollectionReference orderRef = db.collection("order");

        orderRef.document(orderId)
                .get()
                .addOnSuccessListener(orderDoc -> {
                    if (orderDoc.exists()) {
                        currentOrderId = orderDoc.getId();
                        Log.d("CartDebug", "Order document found: " + currentOrderId);

                        List<String> foodIds = (List<String>) orderDoc.get("food_id");
                        List<Long> quantityObjs = (List<Long>) orderDoc.get("quantity");
                        List<Double> rateObjs = (List<Double>) orderDoc.get("rate");
                        List<Long> estTimeObjs = (List<Long>) orderDoc.get("est_time");
                        Double amount = orderDoc.getDouble("amount");

                        if (foodIds == null || foodIds.isEmpty()) {
                            cartItems.clear();
                            cartAdapter.notifyDataSetChanged();
                            Toast.makeText(Cart.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        cartItems.clear();
                        List<CartItem> tempCartItems = new ArrayList<>();
                        int totalItems = foodIds.size();
                        final int[] fetchedCount = {0};

                        for (int i = 0; i < totalItems; i++) {
                            String foodId = foodIds.get(i);
                            int quantity = (quantityObjs != null && i < quantityObjs.size()) ? quantityObjs.get(i).intValue() : 0;
                            double rate = (rateObjs != null && i < rateObjs.size()) ? rateObjs.get(i) : 0.0;
                            int estTime = (estTimeObjs != null && i < estTimeObjs.size()) ? estTimeObjs.get(i).intValue() : 0;

                            int finalQuantity = quantity;
                            double finalRate = rate;
                            int finalEstTime = estTime;

                            db.collection("food_item").document(foodId)
                                    .get()
                                    .addOnSuccessListener(foodDoc -> {
                                        fetchedCount[0]++;
                                        if (foodDoc.exists()) {
                                            String foodName = "";
                                            Object foodNameObj = foodDoc.get("food_name");
                                            if (foodNameObj != null) foodName = foodNameObj.toString();

                                            String imageUrl = foodDoc.getString("image");

                                            tempCartItems.add(new CartItem(foodName, finalRate, imageUrl, finalQuantity, finalEstTime));
                                        }

                                        if (fetchedCount[0] == totalItems) {
                                            cartItems.clear();
                                            cartItems.addAll(tempCartItems);
                                            cartAdapter.notifyDataSetChanged();

                                            if (amount != null) {
                                                double total = amount;
                                                double subtotal = total - (total * 0.05);

                                                displayAmounts(subtotal, total);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Cart", "Failed to fetch food item: " + foodId, e);
                                    });
                        }

                    } else {
                        cartItems.clear();
                        cartAdapter.notifyDataSetChanged();
                        Toast.makeText(Cart.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Cart", "Failed to fetch cart", e);
                    Toast.makeText(Cart.this, "Error loading cart.", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayAmounts(double subtotal, double total) {
        cartSubtotalTv.setText("Subtotal: ₹" + String.format("%.2f", subtotal));
        cartTotalTv.setText("Total: ₹" + String.format("%.2f", total));
        totalAmount = total; // Save total for payment page
    }
}