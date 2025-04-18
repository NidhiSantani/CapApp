package com.example.capstone_kitchen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class PaymentWallet extends AppCompatActivity {

    EditText pin1, pin2, pin3, pin4;
    private RadioButton rbUPI, rbWallet;
    private boolean isPinVisible = false;
    private ImageButton togglePinVisibility;
    Button btnPlaceOrder;

    TextView tvPaymentamt, tvbalanceamt;
    FirebaseFirestore db;

    String sapid, orderId;
    double totalAmount;

    String fetchedPin = "", paymentId = "";
    int pinAttempts = 0;
    final int MAX_ATTEMPTS = 3;

    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_wallet);

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        togglePinVisibility = findViewById(R.id.togglePinVisibility);

        pin1 = findViewById(R.id.pin1);
        pin2 = findViewById(R.id.pin2);
        pin3 = findViewById(R.id.pin3);
        pin4 = findViewById(R.id.pin4);

        moveCursorOnInput(pin1, pin2);
        moveCursorOnInput(pin2, pin3);
        moveCursorOnInput(pin3, pin4);
        moveCursorOnDelete(pin2, pin1);
        moveCursorOnDelete(pin3, pin2);
        moveCursorOnDelete(pin4, pin3);

        rbUPI = findViewById(R.id.rbUPI);
        rbWallet = findViewById(R.id.rbWallet);

        tvPaymentamt = findViewById(R.id.tvPaymentamt);
        tvbalanceamt = findViewById(R.id.tvbalanceamt);
        db = FirebaseFirestore.getInstance();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Processing...");
        loadingDialog.setCancelable(false);

        Intent intent = getIntent();
        sapid = intent.getStringExtra("sapid");
        orderId = intent.getStringExtra("orderId");
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0);
        tvPaymentamt.setText("₹" + totalAmount);

        // Set default selection
        rbWallet.setChecked(true);
        rbUPI.setChecked(false);

        // UPI click listener
        rbUPI.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rbWallet.setChecked(false); // Uncheck wallet
                Intent upiIntent = new Intent(PaymentWallet.this, Payment.class);
                upiIntent.putExtra("sapid", sapid);
                upiIntent.putExtra("orderId", orderId);
                upiIntent.putExtra("totalAmount", totalAmount);
                startActivity(upiIntent);
                finish(); // close this activity
            }
        });

        // Fetch wallet details
        db.collection("user").document(sapid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Double bal = doc.getDouble("wallet_balance");
                        Long walletPinLong = doc.getLong("wallet_pin");
                        fetchedPin = walletPinLong != null ? walletPinLong.toString() : "";
                        paymentId = doc.getString("wallet_id");

                        if (bal != null) {
                            tvbalanceamt.setText("₹" + bal);
                        } else {
                            tvbalanceamt.setText("₹0.00");
                            Toast.makeText(this, "Wallet balance not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    tvbalanceamt.setText("₹0.00");
                    Toast.makeText(this, "Failed to fetch wallet details", Toast.LENGTH_SHORT).show();
                });

        togglePinVisibility.setOnClickListener(v -> {
            if (isPinVisible) {
                setPinInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                togglePinVisibility.setImageResource(R.drawable.hidewalletpin);
            } else {
                setPinInputType(InputType.TYPE_CLASS_NUMBER);
                togglePinVisibility.setImageResource(R.drawable.showwalletpin);
            }
            isPinVisible = !isPinVisible;
        });

        btnPlaceOrder.setOnClickListener(v -> {
            if (!rbWallet.isChecked()) {
                Toast.makeText(this, "Please select Wallet option", Toast.LENGTH_SHORT).show();
                return;
            }

            String enteredPin = pin1.getText().toString() + pin2.getText().toString() + pin3.getText().toString() + pin4.getText().toString();

            if (enteredPin.length() != 4) {
                Toast.makeText(this, "Enter 4-digit PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!enteredPin.equals(fetchedPin)) {
                pinAttempts++;
                if (pinAttempts >= MAX_ATTEMPTS) {
                    Toast.makeText(this, "Too many incorrect attempts", Toast.LENGTH_LONG).show();
                    btnPlaceOrder.setEnabled(false);
                } else {
                    Toast.makeText(this, "Incorrect PIN. Attempts left: " + (MAX_ATTEMPTS - pinAttempts), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Confirm Payment")
                    .setMessage("Are you sure you want to pay ₹" + totalAmount + " from your wallet?")
                    .setPositiveButton("Yes", (dialog, which) -> proceedWithPayment())
                    .setNegativeButton("No", null)
                    .show();
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(PaymentWallet.this, Payment.class);
            backIntent.putExtra("sapid", sapid);
            backIntent.putExtra("orderId", orderId);
            backIntent.putExtra("totalAmount", totalAmount);
            startActivity(backIntent);
        });

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
                Intent cartIntent = new Intent(this, Cart.class);
                cartIntent.putExtra("sapid", sapid);
                startActivity(cartIntent);
                return true;
            }
            return false;
        });
    }

    private void proceedWithPayment() {
        loadingDialog.show();

        DocumentReference userRef = db.collection("user").document(sapid);
        DocumentReference orderRef = db.collection("order").document(orderId);
        DocumentReference paymentRef = db.collection("payment").document(); // Create a new document for payment

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot userSnap = transaction.get(userRef);
            Double currentBalanceObj = userSnap.getDouble("wallet_balance");

            if (currentBalanceObj == null) {
                throw new FirebaseFirestoreException("Wallet balance missing", FirebaseFirestoreException.Code.ABORTED);
            }

            double currentBalance = currentBalanceObj;
            if (currentBalance < totalAmount) {
                throw new FirebaseFirestoreException("Insufficient balance", FirebaseFirestoreException.Code.ABORTED);
            }

            double updatedBalance = currentBalance - totalAmount;
            transaction.update(userRef, "wallet_balance", updatedBalance);
            transaction.update(orderRef, "status", "Placed");
            transaction.update(orderRef, "payment_id", paymentId);

            // Create a new payment record in the payment collection
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("payment_id", paymentId);
            paymentData.put("amount", totalAmount);
            paymentData.put("mode", "Wallet"); // Or another method like "UPI" if needed
            paymentData.put("payment_status", "Paid");
            paymentData.put("date", Timestamp.now()); // You can store the timestamp in Unix format
            paymentData.put("order_id", orderRef); // <-- Reference to order document
            paymentData.put("user_id", userRef);   // <-- Reference to user document

            // Add payment record to the payment collection
            transaction.set(paymentRef, paymentData);

            return null;
        }).addOnSuccessListener(unused -> {
            loadingDialog.dismiss();
            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();

            db.collection("user").document(sapid).get()
                    .addOnSuccessListener(doc -> {
                        Double newBal = doc.getDouble("wallet_balance");
                        if (newBal != null) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Payment Complete")
                                    .setMessage("Remaining Wallet Balance: ₹" + newBal)
                                    .setPositiveButton("OK", (d, w) -> {
                                        Intent successIntent = new Intent(PaymentWallet.this, PaymentSuccess.class);
                                        successIntent.putExtra("sapid", sapid);
                                        successIntent.putExtra("orderId", orderId);
                                        successIntent.putExtra("totalAmount", totalAmount);
                                        startActivity(successIntent);
                                        finish();
                                    })
                                    .show();
                        }
                    });
        }).addOnFailureListener(e -> {
            loadingDialog.dismiss();
            Toast.makeText(this, "Payment failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void setPinInputType(int inputType) {
        pin1.setInputType(inputType);
        pin2.setInputType(inputType);
        pin3.setInputType(inputType);
        pin4.setInputType(inputType);
        pin1.setSelection(pin1.getText().length());
        pin2.setSelection(pin2.getText().length());
        pin3.setSelection(pin3.getText().length());
        pin4.setSelection(pin4.getText().length());
    }

    private void moveCursorOnInput(EditText current, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) next.requestFocus();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void moveCursorOnDelete(EditText current, EditText previous) {
        current.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && current.getText().length() == 0) {
                previous.requestFocus();
            }
            return false;
        });
    }
}