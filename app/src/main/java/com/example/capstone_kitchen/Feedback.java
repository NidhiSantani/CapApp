package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    private EditText feedbackInput;
    private TextView wordCount;
    private Button submitButton;
    private Spinner categoryDropdown, foodDropdown;
    private ImageView[] stars = new ImageView[5];
    private int rating = 0;
    private static final int MAX_WORD_LIMIT = 100;
    private String selectedCuisineId = "";
    private String selectedFoodId = "";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.feedback);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String sapid = getIntent().getStringExtra("sapid");
        db = FirebaseFirestore.getInstance();

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottomnav_home) {
                startActivity(new Intent(Feedback.this, HomePage.class).putExtra("sapid", sapid));
                return true;
            } else if (itemId == R.id.bottomnav_favorites) {
                startActivity(new Intent(Feedback.this, Favorites.class));
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
                startActivity(new Intent(Feedback.this, Cart.class));
                return true;
            }
            return false;
        });

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(view -> startActivity(new Intent(Feedback.this, SideNavigation.class)));

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> startActivity(new Intent(Feedback.this, HomePage.class)));

        feedbackInput = findViewById(R.id.feedbackInput);
        wordCount = findViewById(R.id.wordCount);
        submitButton = findViewById(R.id.submitPinButton);
        submitButton.setEnabled(false);
        submitButton.setAlpha(0.5f);

        feedbackInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateWordCount(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });

        categoryDropdown = findViewById(R.id.categoryDropdown);
        foodDropdown = findViewById(R.id.foodDropdown);
        setupDropdowns();

        setupStarRating();

        submitButton.setOnClickListener(v -> {
            String selectedCuisine = categoryDropdown.getSelectedItem().toString();
            String selectedFood = foodDropdown.getSelectedItem().toString();
            String comment = feedbackInput.getText().toString().trim();

            if (selectedCuisine.equals("Select Cuisine")) {
                Toast.makeText(this, "Please select a cuisine category", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedFood.equals("Select Food Item") || selectedFoodId.isEmpty()) {
                Toast.makeText(this, "Please select a food item", Toast.LENGTH_SHORT).show();
                return;
            }
            if (comment.isEmpty()) {
                Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rating == 0) {
                Toast.makeText(this, "Please give a star rating", Toast.LENGTH_SHORT).show();
                return;
            }

            // Submit feedback to Firestore
            Map<String, Object> review = new HashMap<>();
            review.put("comment", comment);
            review.put("rating", rating);
            review.put("created_at", Timestamp.now());
            review.put("user_id", db.document("user/" + sapid));
            review.put("food_id", db.document("food_item/" + selectedFoodId));

            db.collection("review")
                    .add(review)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Feedback submitted!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Feedback.this, FeedbackSubmit.class).putExtra("sapid", sapid));
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit feedback.", Toast.LENGTH_SHORT).show());
        });
    }

    private void updateWordCount(String text) {
        String[] words = text.trim().split("\\s+");
        int wordCountValue = text.trim().isEmpty() ? 0 : words.length;
        wordCount.setText(wordCountValue + "/100 words");

        if (wordCountValue > MAX_WORD_LIMIT) {
            Toast.makeText(this, "Word limit of 100 reached!", Toast.LENGTH_SHORT).show();
            feedbackInput.setText(text.substring(0, text.lastIndexOf(" ")));
            feedbackInput.setSelection(feedbackInput.getText().length());
        }

        boolean hasText = wordCountValue > 0;
        submitButton.setEnabled(hasText);
        submitButton.setAlpha(hasText ? 1.0f : 0.5f);
    }

    private void setupDropdowns() {
        db.collection("cuisine")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> cuisineList = new ArrayList<>();
                        cuisineList.add("Select Cuisine");
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String cuisineName = doc.getString("cuisine_name");
                            if (cuisineName != null) cuisineList.add(cuisineName);
                        }
                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cuisineList);
                        categoryDropdown.setAdapter(categoryAdapter);

                        categoryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedCuisine = categoryDropdown.getSelectedItem().toString();
                                if (!selectedCuisine.equals("Select Cuisine")) {
                                    fetchCuisineIdAndUpdateFoodDropdown(selectedCuisine);
                                } else {
                                    List<String> emptyFoodList = new ArrayList<>();
                                    emptyFoodList.add("Select Food Item");
                                    ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(Feedback.this, android.R.layout.simple_spinner_dropdown_item, emptyFoodList);
                                    foodDropdown.setAdapter(foodAdapter);
                                }
                            }
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    } else {
                        Toast.makeText(this, "Error getting cuisines", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCuisineIdAndUpdateFoodDropdown(String selectedCuisine) {
        db.collection("cuisine")
                .whereEqualTo("cuisine_name", selectedCuisine)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot cuisineDoc = task.getResult().getDocuments().get(0);
                        selectedCuisineId = cuisineDoc.getId();
                        updateFoodDropdown();
                    } else {
                        Toast.makeText(this, "Cuisine not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateFoodDropdown() {
        if (selectedCuisineId.isEmpty()) return;
        db.collection("food_item")
                .whereEqualTo("cuisine_id", db.document("cuisine/" + selectedCuisineId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> foodList = new ArrayList<>();
                        foodList.add("Select Food Item");
                        Map<String, String> foodNameToIdMap = new HashMap<>();
                        for (QueryDocumentSnapshot foodDoc : task.getResult()) {
                            String foodName = foodDoc.getString("food_name");
                            if (foodName != null) {
                                foodList.add(foodName);
                                foodNameToIdMap.put(foodName, foodDoc.getId());
                            }
                        }
                        ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(Feedback.this, android.R.layout.simple_spinner_dropdown_item, foodList);
                        foodDropdown.setAdapter(foodAdapter);

                        foodDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedFood = foodDropdown.getSelectedItem().toString();
                                selectedFoodId = foodNameToIdMap.getOrDefault(selectedFood, "");
                            }
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    } else {
                        Toast.makeText(this, "Error getting food items", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupStarRating() {
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);

        for (int i = 0; i < stars.length; i++) {
            int index = i;
            stars[i].setOnClickListener(v -> setRating(index + 1));
        }
    }

    private void setRating(int newRating) {
        rating = newRating;
        for (int i = 0; i < stars.length; i++) {
            stars[i].setImageResource(i < rating ? R.drawable.star_filled : R.drawable.star_unfilled);
        }
    }
}