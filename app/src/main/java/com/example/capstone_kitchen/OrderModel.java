package com.example.capstone_kitchen;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderModel {

    private final FirebaseFirestore db;
    private final String sapid;
    private String currentOrderId;

    public OrderModel(String sapid) {
        this.sapid = sapid;
        this.db = FirebaseFirestore.getInstance();
        fetchOrCreateUnplacedOrder(orderId -> currentOrderId = orderId);
    }

    private void fetchOrCreateUnplacedOrder(OrderFetchCallback callback) {
        db.collection("order")
                .whereEqualTo("sapid", sapid)
                .whereEqualTo("status", "Unplaced")
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        currentOrderId = snapshot.getDocuments().get(0).getId();
                        callback.onOrderFetched(currentOrderId);
                    } else {
                        createNewOrder(callback);
                    }
                })
                .addOnFailureListener(e -> Log.e("OrderModel", "Error fetching unplaced order", e));
    }

    private void createNewOrder(OrderFetchCallback callback) {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("sapid", sapid);
        orderData.put("status", "Unplaced");
        orderData.put("amount", 0.0);
        orderData.put("counter_id", new ArrayList<Integer>());
        orderData.put("est_time", new ArrayList<Integer>());
        orderData.put("food_id", new ArrayList<String>());
        orderData.put("order_date", Timestamp.now());
        orderData.put("payment_id", "");
        orderData.put("quantity", new ArrayList<Integer>());
        orderData.put("rate", new ArrayList<Double>());
        orderData.put("user_id", db.collection("user").document(sapid));

        db.collection("order")
                .add(orderData)
                .addOnSuccessListener(docRef -> {
                    currentOrderId = docRef.getId();
                    callback.onOrderFetched(currentOrderId);
                })
                .addOnFailureListener(e -> Log.e("OrderModel", "Error creating new order", e));
    }

    public void addFoodToOrder(String foodId, int quantity, double rate, int baseEstTime) {
        if (currentOrderId == null) {
            fetchOrCreateUnplacedOrder(orderId -> currentOrderId = orderId);
            return;
        }

        DocumentReference orderRef = db.collection("order").document(currentOrderId);

        orderRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) return;

            ArrayList<String> foodIds = (ArrayList<String>) documentSnapshot.get("food_id");
            ArrayList<Integer> quantities = castToIntList(documentSnapshot.get("quantity"));
            ArrayList<Double> rates = castToDoubleList(documentSnapshot.get("rate"));
            ArrayList<Integer> estTimes = castToIntList(documentSnapshot.get("est_time"));
            ArrayList<Integer> counterIds = castToIntList(documentSnapshot.get("counter_id"));

            if (foodIds == null) foodIds = new ArrayList<>();
            if (quantities == null) quantities = new ArrayList<>();
            if (rates == null) rates = new ArrayList<>();
            if (estTimes == null) estTimes = new ArrayList<>();
            if (counterIds == null) counterIds = new ArrayList<>();

            // Wrapping in effectively final array for access in nested callbacks
            ArrayList<String> finalFoodIds = foodIds;
            ArrayList<Integer> finalQuantities = quantities;
            ArrayList<Double> finalRates = rates;
            ArrayList<Integer> finalEstTimes = estTimes;
            ArrayList<Integer> finalCounterIds = counterIds;

            int foodIndex = finalFoodIds.indexOf(foodId);

            if (foodIndex != -1) {
                int currentQuantity = finalQuantities.get(foodIndex);
                finalQuantities.set(foodIndex, currentQuantity + quantity);
                finalEstTimes.set(foodIndex, baseEstTime * (currentQuantity + quantity));

                double totalAmount = calculateTotalAmount(finalRates, finalQuantities);
                updateOrder(orderRef, finalFoodIds, finalQuantities, finalRates, finalEstTimes, finalCounterIds, totalAmount);

            } else {
                finalFoodIds.add(foodId);
                finalQuantities.add(quantity);
                finalRates.add(rate);
                finalEstTimes.add(baseEstTime * quantity);

                db.collection("food_item").document(foodId).get().addOnSuccessListener(foodSnap -> {
                    DocumentReference cuisineRef = foodSnap.getDocumentReference("cuisine_id");

                    if (cuisineRef != null) {
                        cuisineRef.get().addOnSuccessListener(cuisineSnap -> {
                            DocumentReference counterRef = cuisineSnap.getDocumentReference("counter_id");

                            if (counterRef != null) {
                                counterRef.get().addOnSuccessListener(counterSnap -> {
                                    String counterId = counterSnap.getId(); // Assuming this is the desired counter ID
                                    int counterNumber = extractCounterNumber(counterId);

                                    finalCounterIds.add(counterNumber);

                                    double totalAmount = calculateTotalAmount(finalRates, finalQuantities);
                                    updateOrder(orderRef, finalFoodIds, finalQuantities, finalRates, finalEstTimes, finalCounterIds, totalAmount);
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateOrder(DocumentReference orderRef,
                             ArrayList<String> foodIds,
                             ArrayList<Integer> quantities,
                             ArrayList<Double> rates,
                             ArrayList<Integer> estTimes,
                             ArrayList<Integer> counterIds,
                             double totalAmount) {

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("food_id", foodIds);
        updateData.put("quantity", quantities);
        updateData.put("rate", rates);
        updateData.put("est_time", estTimes);
        updateData.put("counter_id", counterIds);
        updateData.put("amount", totalAmount);

        orderRef.update(updateData)
                .addOnSuccessListener(aVoid -> checkAndDeleteEmptyOrder(orderRef))
                .addOnFailureListener(e -> Log.e("OrderModel", "Update failed", e));
    }

    private double calculateTotalAmount(ArrayList<Double> rates, ArrayList<Integer> quantities) {
        double total = 0.0;
        for (int i = 0; i < rates.size(); i++) {
            total += rates.get(i) * quantities.get(i);
        }
        return total;
    }

    private int extractCounterNumber(String counterId) {
        try {
            return Integer.parseInt(counterId.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public void updateOrderItemQuantity(String foodId, int quantity) {
        if (currentOrderId == null) {
            fetchOrCreateUnplacedOrder(orderId -> currentOrderId = orderId);
            return;
        }

        DocumentReference orderRef = db.collection("order").document(currentOrderId);

        orderRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) return;

            ArrayList<String> foodIds = (ArrayList<String>) documentSnapshot.get("food_id");
            ArrayList<Integer> quantities = castToIntList(documentSnapshot.get("quantity"));
            ArrayList<Double> rates = castToDoubleList(documentSnapshot.get("rate"));
            ArrayList<Integer> estTimes = castToIntList(documentSnapshot.get("est_time"));
            ArrayList<Integer> counterIds = castToIntList(documentSnapshot.get("counter_id"));

            if (foodIds == null || quantities == null || rates == null || estTimes == null) return;

            int foodIndex = foodIds.indexOf(foodId);
            if (foodIndex != -1) {
                double rate = rates.get(foodIndex);
                int baseTime = estTimes.get(foodIndex) / Math.max(quantities.get(foodIndex), 1);

                if (quantity == 0) {
                    foodIds.remove(foodIndex);
                    quantities.remove(foodIndex);
                    rates.remove(foodIndex);
                    estTimes.remove(foodIndex);
                    if (counterIds != null && foodIndex < counterIds.size()) {
                        counterIds.remove(foodIndex);
                    }
                } else {
                    quantities.set(foodIndex, quantity);
                    estTimes.set(foodIndex, baseTime * quantity);
                }

                double newAmount = calculateTotalAmount(rates, quantities);
                updateOrder(orderRef, foodIds, quantities, rates, estTimes, counterIds, newAmount);
            }
        });
    }

    private void checkAndDeleteEmptyOrder(DocumentReference orderRef) {
        orderRef.get().addOnSuccessListener(documentSnapshot -> {
            ArrayList<String> foodIds = (ArrayList<String>) documentSnapshot.get("food_id");
            ArrayList<Integer> quantities = castToIntList(documentSnapshot.get("quantity"));
            ArrayList<Integer> estTimes = castToIntList(documentSnapshot.get("est_time"));
            ArrayList<Double> rates = castToDoubleList(documentSnapshot.get("rate"));

            if (foodIds.isEmpty() && quantities.isEmpty() && estTimes.isEmpty() && rates.isEmpty()) {
                orderRef.delete()
                        .addOnSuccessListener(aVoid -> Log.d("OrderModel", "Empty order deleted."))
                        .addOnFailureListener(e -> Log.e("OrderModel", "Failed to delete empty order.", e));
            }
        });
    }

    public void getItemQuantity(String foodId, QuantityCallback callback) {
        if (currentOrderId == null) {
            fetchOrCreateUnplacedOrder(orderId -> {
                currentOrderId = orderId;
                callback.onQuantityFetched(0);
            });
            return;
        }

        DocumentReference orderRef = db.collection("order").document(currentOrderId);

        orderRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                callback.onQuantityFetched(0);
                return;
            }

            ArrayList<String> foodIds = (ArrayList<String>) documentSnapshot.get("food_id");
            ArrayList<Integer> quantities = castToIntList(documentSnapshot.get("quantity"));

            if (foodIds == null || quantities == null) {
                callback.onQuantityFetched(0);
                return;
            }

            int index = foodIds.indexOf(foodId);
            callback.onQuantityFetched(index != -1 ? quantities.get(index) : 0);
        }).addOnFailureListener(e -> callback.onQuantityFetched(0));
    }

    private ArrayList<Integer> castToIntList(Object rawList) {
        ArrayList<Integer> result = new ArrayList<>();
        if (rawList instanceof ArrayList<?>) {
            for (Object obj : (ArrayList<?>) rawList) {
                if (obj instanceof Number) {
                    result.add(((Number) obj).intValue());
                }
            }
        }
        return result;
    }

    private ArrayList<Double> castToDoubleList(Object rawList) {
        ArrayList<Double> result = new ArrayList<>();
        if (rawList instanceof ArrayList<?>) {
            for (Object obj : (ArrayList<?>) rawList) {
                if (obj instanceof Number) {
                    result.add(((Number) obj).doubleValue());
                }
            }
        }
        return result;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public String getOrderId() {
        return currentOrderId;
    }

    public interface QuantityCallback {
        void onQuantityFetched(int quantity);
    }

    public interface OrderFetchCallback {
        void onOrderFetched(String orderId);
    }
}