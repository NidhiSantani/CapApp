package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewBestsellers;
    private CategoryAdapter categoryAdapter;
    private BestsellerAdapter bestsellerAdapter;
    private List<Category> categoryList;
    private List<Bestseller> bestsellerList;
    private FirebaseFirestore db;
    private String orderId;  // Declare orderId here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        String sapid = getIntent().getStringExtra("sapid");

        TextView greetingText = findViewById(R.id.greetingname);
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            greetingText.setText(username);
        }

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Adjust insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Fetch orderId from Firestore based on sapid and status
        fetchOrderIdFromFirestore(sapid);

        // ---------------- Setup Categories RecyclerView ----------------
        recyclerViewCategories = findViewById(R.id.categories_recycler);
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        categoryList = new ArrayList<>();

        // Handle click to open MenuPage
        categoryAdapter = new CategoryAdapter(this, categoryList, category -> {
            Intent intent = new Intent(HomePage.this, MenuPage.class);
            intent.putExtra("category_name", category.getName());
            intent.putExtra("category_image", category.getImageUrl());
            intent.putExtra("cuisine_id", category.getCuisineId());  // Pass cuisine_id (Firestore document reference) to MenuPage
            intent.putExtra("sapid",sapid);
            intent.putExtra("orderId", orderId);  // Pass orderId to MenuPage
            startActivity(intent);
        });

        recyclerViewCategories.setAdapter(categoryAdapter);

        // Fetch categories from Firestore
        fetchCategoriesFromFirestore();

        // ---------------- Setup Bestsellers RecyclerView ----------------
        recyclerViewBestsellers = findViewById(R.id.bestsellers_recycler);
        recyclerViewBestsellers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;  // Disable horizontal scrolling
            }
        };

        bestsellerList = new ArrayList<>();
        bestsellerAdapter = new BestsellerAdapter(this, bestsellerList);
        recyclerViewBestsellers.setAdapter(bestsellerAdapter);

        // Fetch bestsellers from Firestore
        fetchBestsellersFromFirestore();

        ImageButton menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(v -> {
            // Create an Intent to start SideNavigation activity
            Intent intent = new Intent(HomePage.this, SideNavigation.class);
            // Put the SAP ID into the Intent to pass it to SideNavigation activity
            intent.putExtra("sapid", sapid);
            // Start SideNavigation activity
            startActivity(intent);
        });

        // ---------------- Setup Bottom Navigation ----------------
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottomnav_home) {
                Intent intent = new Intent(HomePage.this, HomePage.class);
                intent.putExtra("sapid", sapid);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.bottomnav_favorites) {
                Intent intent = new Intent(HomePage.this, Favorites.class);
                intent.putExtra("sapid", sapid);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.bottomnav_wallet) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("user").document(sapid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Long walletPin = documentSnapshot.getLong("wallet_pin");
                                    Intent intent;
                                    if (walletPin == null || walletPin == 0) {
                                        // No PIN set
                                        intent = new Intent(HomePage.this, VirtualWallet.class);
                                    } else {
                                        // PIN already set
                                        intent = new Intent(HomePage.this, WalletDisplay.class);
                                    }
                                    intent.putExtra("sapid", sapid);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(HomePage.this, "User not found", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(HomePage.this, "Failed to access wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    return true;
            } else if (itemId == R.id.bottomnav_cart) {
                if (orderId == null || orderId.isEmpty()) {
                    Toast.makeText(HomePage.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(HomePage.this, Cart.class);
                    intent.putExtra("sapid", sapid);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                }
                return true;
            }

            return false;
        });
    }

    private void fetchCategoriesFromFirestore() {
        // Reference to the "cuisine" collection in Firestore
        CollectionReference categoriesRef = db.collection("cuisine");

        // Query Firestore to get category data
        categoriesRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            int totalCuisines = querySnapshot.size();  // Get the number of documents fetched
                            Log.d("HomePage", "Total cuisines fetched: " + totalCuisines);  // Log the count

                            if (totalCuisines > 0) {
                                categoryList.clear();  // Clear any existing data before adding new data

                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    String name = document.getString("cuisine_name");
                                    String imageUrl = document.getString("cuisine_image");
                                    String cuisineId = document.getId();

                                    // Add the category with image URL to the list
                                    categoryList.add(new Category(name, imageUrl, cuisineId));
                                }

                                // Log the number of categories in the list after adding them
                                Log.d("HomePage", "Number of categories in list: " + categoryList.size());

                                categoryAdapter.notifyDataSetChanged();  // Notify adapter to refresh the list
                            } else {
                                Log.w("HomePage", "No cuisines found in Firestore.");
                            }
                        } else {
                            Log.w("HomePage", "Firestore query returned null results.");
                        }
                    } else {
                        Log.w("HomePage", "Error getting documents.", task.getException());
                    }
                });
    }

    // Method to fetch orderId based on sapid and status = "Unplaced"
    private void fetchOrderIdFromFirestore(String sapid) {
        CollectionReference ordersRef = db.collection("order");
        ordersRef.whereEqualTo("sapid", sapid)
                .whereEqualTo("status", "Unplaced")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Get the first matching document
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            orderId = document.getId();  // Assign the orderId
                            Log.d("HomePage", "Order ID fetched: " + orderId);  // Log the orderId
                        } else {
                            Log.w("HomePage", "No unplaced orders found for sapid: " + sapid);
                        }
                    } else {
                        Log.w("HomePage", "Error getting orderId from Firestore.", task.getException());
                    }
                });
    }

    // Method to fetch bestsellers from Firestore
    private void fetchBestsellersFromFirestore() {
        CollectionReference foodItemRef = db.collection("food_item");  // Reference to food_item collection

        foodItemRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            bestsellerList.clear();  // Clear any existing data before adding new data

                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String name = document.getString("food_name");  // Get the food name

                                // Add the item to the list
                                bestsellerList.add(new Bestseller(name, R.drawable.foodph));

                            }

                            // Shuffle the list and get only the first 3 items
                            Collections.shuffle(bestsellerList);  // Shuffle the list
                            if (bestsellerList.size() > 3) {
                                bestsellerList = new ArrayList<>(bestsellerList.subList(0, 3));  // Limit to only 3 items by creating a new list
                            }

                            bestsellerAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the list
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching food items", task.getException());
                    }
                });
    }


    private int getImageResIdFromUrl(String imageUrl) {
        return R.drawable.foodph; // Placeholder image until image loading is set up
    }
}