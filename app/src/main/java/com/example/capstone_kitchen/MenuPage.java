package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuPage extends AppCompatActivity implements FilterAdapter.OnFilterClickListener {

    private RecyclerView filterRecycler;
    private FilterAdapter filterAdapter;
    private List<Filter> filterList;

    private RecyclerView foodRecycler;
    private FoodAdapter foodAdapter;
    private List<FoodMenu> foodList;

    private FirebaseFirestore db;
    private String cuisineId;
    private String sapid;
    private List<String> userFavorites = new ArrayList<>();
    private OrderModel orderModel; // Will be used for order tracking

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        db = FirebaseFirestore.getInstance();

        cuisineId = getIntent().getStringExtra("cuisine_id");
        sapid = getIntent().getStringExtra("sapid");

        if (cuisineId == null || sapid == null) {
            Log.e("MenuPage", "Cuisine ID or SAPID is null, cannot proceed");
            return;
        }

        orderModel = new OrderModel(sapid); // Initialize order model with sapid

        // Set up filter RecyclerView
        filterRecycler = findViewById(R.id.filter_recycler);
        filterRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        filterList = new ArrayList<>();
        filterAdapter = new FilterAdapter(this, filterList, this);
        filterRecycler.setAdapter(filterAdapter);

        // Set up food item RecyclerView
        foodRecycler = findViewById(R.id.food_recycler);
        foodRecycler.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();

        // Initialize the adapter here
        foodAdapter = new FoodAdapter(foodList, sapid, userFavorites, orderModel);
        foodRecycler.setAdapter(foodAdapter);  // Ensure adapter is attached before fetching data

        // Now fetch user favorites
        fetchUserFavorites(); // Will only update data now, not adapter

        // Fetch filters from Firestore
        loadFiltersFromFirestore();

        // Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.bottomnav_home) {
                intent = new Intent(MenuPage.this, HomePage.class);
                intent.putExtra("orderId", orderModel.getOrderId());
            } else if (itemId == R.id.bottomnav_favorites) {
                intent = new Intent(MenuPage.this, Favorites.class);
                intent.putExtra("sapid", sapid);
            } else if (itemId == R.id.bottomnav_wallet) {
                intent = new Intent(MenuPage.this, VirtualWallet.class);
            } else if (itemId == R.id.bottomnav_cart) {
                intent = new Intent(MenuPage.this, Cart.class);
                intent.putExtra("orderId", orderModel.getOrderId());  // Pass the orderId to Cart
            } else {
                return false;
            }
            startActivity(intent);
            return true;
        });
    }

    private void fetchUserFavorites() {
        db.collection("user").document(sapid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Object favorites = documentSnapshot.get("favorites");

                        if (favorites instanceof List<?>) {
                            // If 'favorites' is a List, cast it to List<String>
                            List<String> favoriteList = (List<String>) favorites;
                            userFavorites.clear();
                            userFavorites.addAll(favoriteList);
                        } else if (favorites instanceof Map<?, ?>) {
                            // Handle the case where 'favorites' is a Map
                            Log.e("MenuPage", "Favorites is a Map, not a List: " + favorites);

                            // Convert Map to List (assuming the map contains food IDs as keys/values)
                            List<String> favoriteList = new ArrayList<>();
                            for (Object key : ((Map<?, ?>) favorites).keySet()) {
                                if (key instanceof String) {
                                    favoriteList.add((String) key);
                                }
                            }

                            // Update userFavorites with the converted List
                            userFavorites.clear();
                            userFavorites.addAll(favoriteList);
                        } else {
                            Log.e("MenuPage", "Unexpected data type for favorites: " + favorites);
                        }
                    } else {
                        Log.e("MenuPage", "Document for the user doesn't exist.");
                    }

                    // After fetching user favorites, we fetch food items
                    fetchFoodItemsFromFirestore(); // Only update data now, not adapter
                })
                .addOnFailureListener(e -> Log.e("MenuPage", "Failed to fetch favorites", e));
    }

    private void fetchFoodItemsFromFirestore() {
        if (cuisineId == null) {
            Log.w("MenuPage", "Cuisine ID is null. Skipping food fetch.");
            return;
        }

        DocumentReference cuisineRef = db.collection("cuisine").document(cuisineId);
        db.collection("food_item")
                .whereEqualTo("cuisine_id", cuisineRef)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    foodList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String name = doc.getString("food_name");
                        String description = doc.getString("description");
                        double rate = doc.getDouble("rate") != null ? doc.getDouble("rate") : 0.0;
                        Long timeLong = doc.getLong("make_time");
                        int time = timeLong != null ? timeLong.intValue() : 0;
                        String imageUrl = doc.getString("image");
                        String foodId = doc.getId();
                        boolean availabilityStatus = doc.getBoolean("availability_status") != null && doc.getBoolean("availability_status");

                        // Ensure no null or invalid data is passed
                        name = name != null ? name : "Unknown Food Name";
                        description = description != null ? description : "No Description Available";
                        imageUrl = imageUrl != null ? imageUrl : "android.resource://com.example.capstone_kitchen/" + R.drawable.foodph;

                        // Create FoodMenu object with the fetched data
                        FoodMenu food = new FoodMenu(foodId, name, description, rate, time, imageUrl, availabilityStatus, 0);
                        foodList.add(food);
                    }

                    // Notify the adapter that the data has changed
                    foodAdapter.notifyDataSetChanged(); // Notify the adapter to refresh
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Failed to fetch food items", e));
    }

    private void loadFiltersFromFirestore() {
        db.collection("cuisine")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String cuisineName = doc.getString("cuisine_name");
                            String imageUrl = doc.getString("image");
                            String cuisineDocId = doc.getId();

                            if (cuisineName != null) {
                                if (imageUrl == null || imageUrl.isEmpty()) {
                                    imageUrl = "android.resource://com.example.capstone_kitchen/" + R.drawable.foodph;
                                }
                                filterList.add(new Filter(cuisineName, imageUrl, false, cuisineDocId));
                            }
                        }
                        filterAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("MenuPage", "No cuisines found in Firestore.");
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Failed to fetch filters", e));
    }

    @Override
    public void onFilterClick(String cuisineId) {
        if (cuisineId != null && !cuisineId.isEmpty()) {
            this.cuisineId = cuisineId;
            fetchFoodItemsFromFirestore();
        } else {
            Log.w("MenuPage", "Invalid cuisineId received: " + cuisineId);
        }
    }
}