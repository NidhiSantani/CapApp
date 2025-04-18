package com.example.capstone_kitchen;

import android.content.Intent;
import android.content.res.AssetManager;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private RecyclerView recommendationRecyclerView;
    private RecommendationAdapter recommendationAdapter;
    private List<RecommendationItem> recommendationList;


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


        recommendationRecyclerView = findViewById(R.id.recommendationRecyclerView);
        recommendationList = new ArrayList<>();
        recommendationAdapter = new RecommendationAdapter(this, recommendationList);
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recommendationRecyclerView.setLayoutManager(horizontalLayout);
        recommendationRecyclerView.setAdapter(recommendationAdapter);


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
        List<String> cartFoodNames = new ArrayList<>();

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
                                            cartFoodNames.add(foodName); // Assuming 'name' is the food_name fetched from Firestore


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

                                            if (!cartFoodNames.isEmpty()) {
                                                String firstFoodName = cartFoodNames.get(0);  // Get the first food name
                                                Log.d("recommend", "Food:" + firstFoodName);
                                                fetchDirectRecommendations(firstFoodName);   // Pass it to the function
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

    private void loadRecommendationsFromJSON(Set<String> cartFoodNames) {
        try {
            Log.d("CartDebug", "Cart food names: " + cartFoodNames.toString());

            // Load the JSON file from assets
            InputStream is = getAssets().open("recommendations.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            Set<String> recommendedNames = new HashSet<>();

            // Parse JSON array
            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String foodName = obj.getString("food_name");

                // Check if food item in the cart matches
                if (cartFoodNames.contains(foodName)) {
                    // Extract recommendations array
                    JSONArray recommendationsArray = obj.getJSONArray("recommendations");
                    for (int j = 0; j < recommendationsArray.length(); j++) {
                        String recommended = recommendationsArray.getString(j).trim();
                        recommendedNames.add(recommended);
                    }
                }
            }

            Log.d("CartDebug", "Recommended food names from JSON: " + recommendedNames.toString());
            fetchRecommendedFoodItemsFromFirestore(recommendedNames);

        } catch (Exception e) {
            Log.e("Cart", "Error loading JSON file: " + e.getMessage());
        }
    }


    private void fetchRecommendedFoodItemsFromFirestore(Set<String> names) {
        db.collection("food_item")
                .get()
                .addOnSuccessListener(query -> {
                    recommendationList.clear();
                    for (DocumentSnapshot doc : query) {
                        String name = doc.getString("food_name");
                        if (name != null && names.contains(name)) {
                            String image = doc.getString("image");
                            double rate = doc.getDouble("rate") != null ? doc.getDouble("rate") : 0.0;

                            Log.d("CartDebug", "Recommended food: " + name + ", Rate: " + rate);

                            recommendationList.add(new RecommendationItem(name, rate, image));
                        }
                    }
                    Log.d("CartDebug", "Final list of recommendations: " + recommendationList.size());

                    recommendationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to load recommendations: " + e.getMessage()));
    }

    private void fetchDirectRecommendations(String cartItemName) {
        Log.d("recommend", "Direct Recommendation Called");
        Map<String, List<String>> recommendationMap = new HashMap<>();

        recommendationMap.put("TOAST WITH BUTTER", Arrays.asList("Masala Omelette with Bread", "Nescafe", "Tea"));
        recommendationMap.put("TOAST, BUTTER & JAM", Arrays.asList("Masala Omelette with Bread", "Nescafe", "Tea"));
        recommendationMap.put("UPMA", Arrays.asList("Nescafe", "Tea", "Plain Uttapam"));
        recommendationMap.put("POTATO POHA", Arrays.asList("Tea", "Double Tea", "Cut Fruit Salad"));
        recommendationMap.put("VADA PAV", Arrays.asList("Nescafe", "Tea", "Potato Poha"));
        recommendationMap.put("MISAL PAV", Arrays.asList("Hot Bournvita", "Potato Poha", "Vada Pav"));
        recommendationMap.put("BATATA WADA (2PCS)", Arrays.asList("Nescafe", "Tea", "Sada Dosa"));
        recommendationMap.put("IDLI SAMBHAR", Arrays.asList("Nescafe", "Tea", "Plain Uttapam"));
        recommendationMap.put("BUTTER IDLI", Arrays.asList("Nescafe", "Upma", "Tomato Onion Uttapam"));
        recommendationMap.put("MEDU WADA SAMBHAR", Arrays.asList("Nescafe", "Tomato Onion Uttapam", "Idli Fry"));
        recommendationMap.put("IDLI/MEDU WADA", Arrays.asList("Nescafe", "Tea", "Sada Dosa"));
        recommendationMap.put("IDLI FRY", Arrays.asList("Nescafe", "Tea", "Sada Dosa"));
        recommendationMap.put("DAHI IDLI", Arrays.asList("Nescafe", "Tea", "Plain Uttapam"));
        recommendationMap.put("SADA DOSA", Arrays.asList("Nescafe", "Upma", "Tomato Onion Uttapam"));
        recommendationMap.put("BUTTER SADA DOSA", Arrays.asList("Nescafe", "Tea", "Idli/Medu Wada"));
        recommendationMap.put("MASALA DOSA", Arrays.asList("Nescafe", "Upma", "Tomato Onion Uttapam"));
        recommendationMap.put("MYSORE SADA DOSA", Arrays.asList("Nescafe", "Tea", "Idli/Medu Wada"));
        recommendationMap.put("MYSORE MASALA DOSA", Arrays.asList("Nescafe", "Upma","Tomato Onion Uttapam"));
        recommendationMap.put("CHEESE SADA DOSA", Arrays.asList("Nescafe", "Tea", "Idli/Medu Wada"));
        recommendationMap.put("CHEESE MASALA DOSA", Arrays.asList("Nescafe", "Tea", "Idli/Medu Wada"));
        recommendationMap.put("PLAIN UTTAPAM", Arrays.asList("Nescafe", "Upma", "Tomato Onion Uttapam"));
        recommendationMap.put("TOMATO UTTAPAM", Arrays.asList("Nescafe", "Upma", "Tomato Onion Uttapam"));
        recommendationMap.put("ONION UTTAPAM", Arrays.asList("Nescafe", "Upma", "Tomato Onion Uttapam"));
        recommendationMap.put("TOMATO ONION UTTAPAM", Arrays.asList("Nescafe", "Tea", "Idli/Medu Wada"));
        recommendationMap.put("SCHEZWAN DOSA", Arrays.asList("Nescafe", "Tea", "Idli/Medu Wada"));
        recommendationMap.put("CHEESE SCHEZWAN DOSA", Arrays.asList("Nescafe", "Tea", "Idli/Medu Wada"));
        recommendationMap.put("VEG PULAO THALI", Arrays.asList("Paneer Pulao", "Paneer Lajawab", "Paneer Peshawari"));
        recommendationMap.put("LUNCH THALI", Arrays.asList("Mushroom Chilli Dry", "Veg Manchurian Dry", "Veg Schezwan Noodles"));
        recommendationMap.put("PAV BHAJI", Arrays.asList("Tea", "Paneer Chilli Dry", "Double Tea"));
        recommendationMap.put("CHEESE PAV BHAJI", Arrays.asList("Tea", "Double Tea", "Cut Fruit Salad"));
        recommendationMap.put("EXTRA PAV", Arrays.asList("Tea", "Paneer Chilli Dry", "Double Tea"));
        recommendationMap.put("VEG CRISPY", Arrays.asList("Veg Hakka Noodles", "Veg Schezwan Noodles", "Chocolate Milk Shake"));
        recommendationMap.put("VEG CHILLI DRY", Arrays.asList("Veg Schezwan Noodles", "Chocolate Milk Shake", "Paneer Chilli Dry"));
        recommendationMap.put("PANEER CHIILY DRY", Arrays.asList("Veg Hakka Noodles", "Chocolate Milk Shake", "Veg Crispy"));
        recommendationMap.put("PANEER SCHEZWAN DRY", Arrays.asList("Paneer Chilli Dry", "Hong Kong Fried Rice", "Veg Crispy"));
        recommendationMap.put("VEG MANCHURIAN DRY", Arrays.asList("Veg Schezwan Noodles", "Schezwan Fried Rice", "Cold Coffee"));
        recommendationMap.put("MUSHROOM CHILLI DRY", Arrays.asList("Paneer Chilli Dry", "Hong Kong Fried Rice", "Veg Crispy"));
        recommendationMap.put("PANEER CRISPY", Arrays.asList("Paneer Chilli Dry", "Hong Kong Fried Rice", "Veg Crispy"));
        recommendationMap.put("IDLI CHILLY DRY", Arrays.asList("Veg Crispy", "Medu Wada Sambhar", "Cold Coffee"));
        recommendationMap.put("IDLI SCHEZWAN DRY", Arrays.asList("Chocolate Milk Shake", "Medu Wada Sambhar", "Cold Coffee"));
        recommendationMap.put("SCHEZWAN CRISPY FRIED POTATO", Arrays.asList("Veg Schezwan Noodles", "Schezwan Fried Rice", "Cold Coffee"));
        recommendationMap.put("CUT FRUIT SALAD", Arrays.asList("Tea", "Cold Coffee", "Masala Dosa"));
        recommendationMap.put("VEG FRIED RICE", Arrays.asList("Paneer Chilly Dry", "Hong Kong Fried Rice", "Veg Crispy"));
        recommendationMap.put("SCHEZWAN FRIED RICE", Arrays.asList("Chocolate Milk Shake", "Veg Crispy", "Paneer Chilly Dry"));
        recommendationMap.put("HONG KONG FRIED RICE", Arrays.asList("Paneer Chilly Dry", "Veg Crispy", "Cold Coffee with Ice Cream"));
        recommendationMap.put("SINGAPORE FRIED RICE", Arrays.asList("Chocolate Milk Shake", "Veg Crispy", "Paneer Chilly Dry"));
        recommendationMap.put("VEG HAKKA NOODLES", Arrays.asList("Paneer Chilly Dry", "Veg Crispy", "Cold Coffee with Ice Cream"));
        recommendationMap.put("VEG SCHEZWAN NOODLES", Arrays.asList("Hong Kong Fried Rice", "Paneer Chiily Dry", "Veg Crispy"));
        recommendationMap.put("VEG HONG KONG NOODLES", Arrays.asList("Paneer Chilly Dry", "Hong Kong Fried Rice", "Veg Crispy"));
        recommendationMap.put("VEG FRANKIE", Arrays.asList("Veg Hakka Noodles", "Hong Kong Fried Rice", "Veg Crispy"));
        recommendationMap.put("PANEER FRANKIE", Arrays.asList("Veg Manchurian Dry", "Veg Schezwan Noodles", "Schezwan Fried Rice"));
        recommendationMap.put("PANEER CHILLI FRANKIE", Arrays.asList("Veg Schezwan Noodles", "Chocolate Milk Shake", "Paneer Chiily Dry"));
        recommendationMap.put("MANCHURIAN FRANKIE", Arrays.asList("Mushroom Chilli Dry", "Veg Manchurian Dry", "Veg Schezwan Noodles"));
        recommendationMap.put("CHOWPATTY BHEL", Arrays.asList("Chinese Bhel", "Dahi Batata Puri", "Pani Puri"));
        recommendationMap.put("CHINESE BHEL", Arrays.asList("Dahi Batata Puri", "Vada Pav", "Lunch Thali"));
        recommendationMap.put("SEV PURI", Arrays.asList("Dahi Batata Puri", "Vada Pav", "Lunch Thali"));
        recommendationMap.put("DAHI BATATA PURI", Arrays.asList("Vada Pav", "Pani Puri", "Lunch Thali"));
        recommendationMap.put("DAHI BHEL", Arrays.asList("Dahi Batata Puri", "Vada Pav", "Lunch Thali"));
        recommendationMap.put("PANI PURI", Arrays.asList("Chinese Bhel", "Dahi Batata Puri", "Lunch Thali"));
        recommendationMap.put("VEG SANDWICH", Arrays.asList("Nescafe", "Hot Milk", "Cold Coffee with Ice Cream"));
        recommendationMap.put("CHEESE PLAIN SANDWICH", Arrays.asList("Fresh Lime Water", "Cold Coffee with Ice Cream", "Pineapple Juice"));
        recommendationMap.put("VEG CHEESE SANDWICH", Arrays.asList("Tea", "Vada Pav", "Cold Coffee"));
        recommendationMap.put("VEG GRILL SANDWICH", Arrays.asList("Fresh Lime Water", "Cold Coffee with Ice Cream", "Pineapple Juice"));
        recommendationMap.put("BREAD BUTTER", Arrays.asList("Tea", "Vada Pav", "Cold Coffee"));
        recommendationMap.put("BREAD BUTTER TOAST", Arrays.asList("Tea", "Vada Pav", "Cold Coffee"));
        recommendationMap.put("BUN MASKA", Arrays.asList("Tea", "Vada Pav", "Cold Coffee"));
        recommendationMap.put("CHEESE TOAST SANDWICH", Arrays.asList("Fresh Lime Water", "Mosambi Juice", "Cold Coffee with Ice Cream"));
        recommendationMap.put("VEG CHEESE TOAST SANDWICH", Arrays.asList("Chocolate Milk Shake", "Cold Coffee with Ice Cream", "Pineapple Juice"));
        recommendationMap.put("MASALA OMELETTE WITH BREAD", Arrays.asList("Tea", "Vada Pav", "Cold Coffee"));
        recommendationMap.put("EGG BHURJI WITH 2 PAV", Arrays.asList("Veg Sandwich", "Veg Crispy", "Veg Grill Sandwich"));
        recommendationMap.put("EGG CURRY WITH RICE", Arrays.asList("Veg Anda Curry", "Paneer Lajawab", "Paneer Peshawari"));
        recommendationMap.put("BOILED EGGS (2 NOS.)", Arrays.asList("Hot Bournvita", "Nescafe", "Tea"));
        recommendationMap.put("FRIED EGGS (2 NOS.) WITH BREAD", Arrays.asList("Hot Bournvita", "Nescafe", "Tea"));
        recommendationMap.put("DAL TADKA", Arrays.asList("Veg Pulao", "Steamed Rice", "Paneer Pulao"));
        recommendationMap.put("DAL FRY", Arrays.asList("Veg Pulao", "Steamed Rice", "Paneer Pulao"));
        recommendationMap.put("TAVA VEG", Arrays.asList("Veg Pulao", "Steamed Rice", "Paneer Pulao"));
        recommendationMap.put("VEG KOLHAPURI", Arrays.asList("Veg Pulao", "Steamed Rice", "Paneer Pulao"));
        recommendationMap.put("PANEER PESHAWARI", Arrays.asList("Veg Pulao", "Steamed Rice", "Paneer Pulao"));
        recommendationMap.put("PANEER LAJAWAB", Arrays.asList("Veg Pulao", "Steamed Rice", "Paneer Pulao"));
        recommendationMap.put("PANEER PASANDA", Arrays.asList("Veg Pulao", "Steamed Rice", "Paneer Pulao"));
        recommendationMap.put("VEG ANDA CURRY", Arrays.asList("Veg Pulao", "Steamed Rice", "Paneer Pulao"));
        recommendationMap.put("STEAMED RICE", Arrays.asList("Veg Anda Curry", "Paneer Lajawab", "Paneer Peshawari"));
        recommendationMap.put("JEERA RICE", Arrays.asList("Veg Anda Curry", "Paneer Lajawab", "Paneer Peshawari"));
        recommendationMap.put("DAL KHICHDI", Arrays.asList("Paneer Pulao", "Veg Anda Curry", "Butter Milk"));
        recommendationMap.put("VEG PULAO", Arrays.asList("Veg Anda Curry", "Paneer Lajawab", "Paneer Peshawari"));
        recommendationMap.put("VEG BIRYANI", Arrays.asList("Veg Anda Curry", "Paneer Lajawab", "Paneer Peshawari"));
        recommendationMap.put("PANEER PULAO", Arrays.asList("Veg Anda Curry", "Paneer Lajawab", "Paneer Peshawari"));
        recommendationMap.put("SWEET LASSI", Arrays.asList("Veg Sandwich", "Veg Pulao", "Paneer Pulao"));
        recommendationMap.put("KESAR LASSI", Arrays.asList("Veg Sandwich", "Veg Pulao", "Paneer Pulao"));
        recommendationMap.put("DRY FRUIT LASSI", Arrays.asList("Veg Sandwich", "Veg Pulao", "Paneer Pulao"));
        recommendationMap.put("MANGO LASSI (SEASONAL)", Arrays.asList("Veg Sandwich", "Veg Pulao", "Paneer Pulao"));
        recommendationMap.put("WATER MELON JUICE", Arrays.asList("Veg Sandwich", "Paneer Chilli Frankie", "Vada Pav"));
        recommendationMap.put("MOSAMBI JUICE", Arrays.asList("Veg Sandwich", "Paneer Chilli Frankie", "Vada Pav"));
        recommendationMap.put("ORANGE JUICE", Arrays.asList("Veg Sandwich", "Paneer Chilli Frankie", "Vada Pav"));
        recommendationMap.put("PINEPAPPLE JUICE", Arrays.asList("Veg Sandwich", "Paneer Chilli Frankie", "Vada Pav"));
        recommendationMap.put("BUTTER MILK", Arrays.asList("Veg Sandwich", "Veg Pulao", "Paneer Pulao"));
        recommendationMap.put("FRESH LIME WATER", Arrays.asList("Hot Bournvita", "Nescafe", "Tea"));
        recommendationMap.put("CHIKOO MILK SHAKE", Arrays.asList("Veg Sandwich", "Potato Poha", "Upma"));
        recommendationMap.put("CHOCOLATE MILK SHAKE", Arrays.asList("Veg Sandwich", "Potato Poha", "Upma"));
        recommendationMap.put("BANANA MILK SHAKE", Arrays.asList("Veg Sandwich", "Potato Poha", "Upma"));
        recommendationMap.put("ROSE MILK SHAKE", Arrays.asList("Misal Pav", "Potato Poha", "Vada Pav"));
        recommendationMap.put("KESAR MILK SHAKE", Arrays.asList("Misal Pav", "Potato Poha", "Vada Pav"));
        recommendationMap.put("COLD COFFEE", Arrays.asList("Veg Sandwich", "Veg Crispy", "Veg Grill Sandwich"));
        recommendationMap.put("COLD COFFEE WITH ICE CREAM", Arrays.asList("Veg Sandwich", "Veg Crispy", "Veg Grill Sandwich"));
        recommendationMap.put("CHIKOO CHOCOLATE MILK SHAKE", Arrays.asList("Veg Sandwich", "Veg Crispy", "Veg Grill Sandwich"));
        recommendationMap.put("TEA", Arrays.asList("Potato Poha", "Sada Dosa", "Vada Pav"));
        recommendationMap.put("NESCAFE", Arrays.asList("Potato Poha", "Sada Dosa", "Vada Pav"));
        recommendationMap.put("HOT MILK", Arrays.asList("Nescafe", "Tea", "Potato Poha"));
        recommendationMap.put("DOUBLE TEA", Arrays.asList("Potato Poha", "Sada Dosa", "Vada Pav"));
        recommendationMap.put("DOUBLE COFFEE", Arrays.asList("Potato Poha", "Sada Dosa", "Vada Pav"));
        recommendationMap.put("HOT CHOCOLATE", Arrays.asList("Hot Bournvita", "Nescafe", "Tea"));
        recommendationMap.put("HOT BOURNVITA", Arrays.asList("Veg Sandwich", "Nescafe", "Hot Milk"));

        List<String> recommendedItems = recommendationMap.get(cartItemName.toUpperCase());
        Log.d("recommend", "Items:" + recommendedItems);

        if (recommendedItems == null || recommendedItems.isEmpty()) {
            Log.d("Recommendations", "No recommendations found for: " + cartItemName);
            return;
        }

        db.collection("food_item")
                .whereIn("food_name", recommendedItems)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    recommendationList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String name = doc.getString("food_name");
                        String image = doc.getString("image");
                        double rate = doc.getDouble("rate") != null ? doc.getDouble("rate") : 0.0;

                        Log.d("recommend", "Food:" + name + rate);

                        recommendationList.add(new RecommendationItem(name, rate, image));
                    }
                    recommendationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Cart", "Failed to fetch direct recommendations: " + e.getMessage());
                });
    }


}