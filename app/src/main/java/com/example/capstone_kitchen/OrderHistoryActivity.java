package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderHistory;
    private ImageView ivBack, ivMenu;
    private OrderHistoryAdapter adapter;
    private final List<OrderHistoryModel> orderList = new ArrayList<>();
    private FirebaseFirestore db;
    private String sapid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ivBack = findViewById(R.id.ivBack);
        ivMenu = findViewById(R.id.ivMenu);
        recyclerViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
        recyclerViewOrderHistory.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderHistoryAdapter(orderList);
        recyclerViewOrderHistory.setAdapter(adapter);

        sapid = getIntent().getStringExtra("sapid");
        db = FirebaseFirestore.getInstance();

        fetchOrders();

        ivBack.setOnClickListener(v -> startActivity(new Intent(OrderHistoryActivity.this, HomePage.class)));
        ivMenu.setOnClickListener(v -> startActivity(new Intent(OrderHistoryActivity.this, SideNavigation.class)));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottomnav_home) {
                startActivity(new Intent(this, HomePage.class));
                return true;
            } else if (itemId == R.id.bottomnav_favorites) {
                startActivity(new Intent(this, Favorites.class));
                return true;
            } else if (itemId == R.id.bottomnav_wallet) {
                startActivity(new Intent(this, VirtualWallet.class));
                return true;
            } else if (itemId == R.id.bottomnav_cart) {
                startActivity(new Intent(this, Cart.class));
                return true;
            }
            return false;
        });
    }

    private void fetchOrders() {
        db.collection("order")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w("OrderHistory", "No orders found for sapid: " + sapid);
                        return;
                    }

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        if (doc.contains("sapid") && sapid.equals(doc.getString("sapid"))) {
                            Timestamp timestamp = doc.getTimestamp("order_date");
                            String formattedDate = formatDate(timestamp);
                            Log.d("OrderHistory", "Formatted Order Date: " + formattedDate);

                            String status = doc.getString("status");
                            List<String> foodIds = (List<String>) doc.get("food_id");
                            List<Long> quantities = (List<Long>) doc.get("quantity");

                            List<?> rawRates = (List<?>) doc.get("rate");
                            List<Double> rates = new ArrayList<>();
                            if (rawRates != null) {
                                for (Object obj : rawRates) {
                                    if (obj instanceof Number) {
                                        rates.add(((Number) obj).doubleValue());
                                    } else {
                                        rates.add(0.0);
                                    }
                                }
                            }

                            if (foodIds == null || foodIds.isEmpty()) continue;

                            List<OrderItemModel> items = new ArrayList<>();
                            final int[] remainingItems = {foodIds.size()};

                            for (int i = 0; i < foodIds.size(); i++) {
                                String foodId = foodIds.get(i);
                                int quantity = (quantities != null && i < quantities.size()) ? quantities.get(i).intValue() : 0;
                                double rate = (rates != null && i < rates.size()) ? rates.get(i) : 0.0;

                                db.collection("food_item").document(foodId).get().addOnSuccessListener(foodDoc -> {
                                    String foodName = foodDoc.getString("name");
                                    items.add(new OrderItemModel(foodName, quantity, rate, foodId));

                                    remainingItems[0]--;
                                    if (remainingItems[0] == 0) {
                                        orderList.add(new OrderHistoryModel(formattedDate, status, items));
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching orders", Toast.LENGTH_SHORT).show();
                    Log.e("OrderHistory", "Error fetching", e);
                });
    }

    private String formatDate(Timestamp timestamp) {
        if (timestamp == null) return "";
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(date).toUpperCase();  // force AM/PM to uppercase
    }
}