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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private List<FoodItem> favoriteList;
    private FirebaseFirestore db;
    private String sapid;
    private TextView emptyText;  // This will reference the "No favorites" TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);

        // Apply insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve sapid
        sapid = getIntent().getStringExtra("sapid");
        if (sapid == null || sapid.isEmpty()) {
            Log.e("Favorites", "SAP ID is missing! Cannot fetch user data.");
            return;
        }

        db = FirebaseFirestore.getInstance();

        // Back button logic
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Favorites.this, HomePage.class);
            intent.putExtra("sapid", sapid);
            startActivity(intent);
        });

        // Bottom navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.bottomnav_home) {
                intent = new Intent(Favorites.this, HomePage.class);
            } else if (itemId == R.id.bottomnav_favorites) {
                return true; // Do nothing if already on Favorites page
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
                intent = new Intent(Favorites.this, Cart.class);
            }

            if (intent != null) {
                intent.putExtra("sapid", sapid);
                startActivity(intent);
                return true;
            }

            return false;
        });

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        favoriteList = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(favoriteList, this, sapid);
        recyclerView.setAdapter(favoriteAdapter);

        // Initialize the empty favorites text view
        emptyText = findViewById(R.id.emptyFavoritesText);
        emptyText.setVisibility(View.GONE);  // Initially hidden

        // Fetch favorite items from Firestore
        fetchFavoriteItems();
    }

    private void fetchFavoriteItems() {
        db.collection("user").document(sapid).get().addOnSuccessListener(documentSnapshot -> {
            List<String> favoriteIds = (List<String>) documentSnapshot.get("favorites");
            if (favoriteIds == null || favoriteIds.isEmpty()) {
                // Show empty state if no favorites
                emptyText.setVisibility(View.VISIBLE);
                return;  // Exit early if no favorites
            }

            // Hide the empty state message if there are favorites
            emptyText.setVisibility(View.GONE);

            // Clear the current list and fetch new favorites
            favoriteList.clear();

            // Log the favorite item IDs being fetched
            for (String foodId : favoriteIds) {
                Log.d("Favorites", "Fetching food item ID: " + foodId);

                db.collection("food_item").document(foodId).get().addOnSuccessListener(foodDoc -> {
                    if (foodDoc.exists()) {
                        String name = foodDoc.getString("food_name");

                        // Fetch and handle 'rate' as Double or String
                        Object rateObject = foodDoc.get("rate");
                        String price = "₹0.00"; // Default value if rate is missing or null
                        if (rateObject instanceof Double) {
                            price = String.format("₹%.2f", (Double) rateObject);
                        } else if (rateObject instanceof Long) {
                            // Convert Long to Double before formatting
                            price = String.format("₹%.2f", ((Long) rateObject).doubleValue());
                        } else if (rateObject instanceof String) {
                            try {
                                price = String.format("₹%.2f", Double.parseDouble((String) rateObject));
                            } catch (NumberFormatException e) {
                                Log.e("Favorites", "Invalid rate format", e);
                            }
                        }

                        String imageUrl = foodDoc.getString("image");

                        // Add food item to the list
                        favoriteList.add(new FoodItem(foodDoc.getId(), name, price, imageUrl));
                        favoriteAdapter.notifyItemInserted(favoriteList.size() - 1); // Better UX with itemInserted
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Log.e("Favorites", "Error fetching favorites", e);
            // Handle error if any
        });
    }
}