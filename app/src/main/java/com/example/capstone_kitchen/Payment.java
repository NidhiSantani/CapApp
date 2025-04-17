package com.example.capstone_kitchen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.TextView;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Payment extends AppCompatActivity {

    private RadioButton rbUPI, rbWallet;
    Button btnPlaceOrder;
    TextView tvPaymentamt;
    String sapid, orderId;
    double totalamount;

    private static final int UPI_PAYMENT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from Intent
        Intent intent = getIntent();
        sapid = intent.getStringExtra("sapid");
        orderId = intent.getStringExtra("orderId");
        totalamount = getIntent().getDoubleExtra("totalAmount", 0.0);

        // Bind the TextView
        tvPaymentamt = findViewById(R.id.tvPaymentamt);
        // Display total amount
        tvPaymentamt.setText("₹" + totalamount);

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        rbUPI = findViewById(R.id.rbUPI);
        rbWallet = findViewById(R.id.rbWallet);

        // Custom RadioGroup behavior
        View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == rbUPI) {
                    rbUPI.setChecked(true);
                    rbWallet.setChecked(false);
                } else if (v == rbWallet) {
                    rbWallet.setChecked(true);
                    rbUPI.setChecked(false);

                    // Intent to navigate to the next page when Wallet is selected
                    Intent walletIntent = new Intent(Payment.this, PaymentWallet.class);
                    walletIntent.putExtra("walletSelected", true);
                    walletIntent.putExtra("sapid", sapid);
                    walletIntent.putExtra("orderId", orderId);
                    walletIntent.putExtra("totalAmount", totalamount);
                    startActivity(walletIntent);
                }
            }
        };

        rbUPI.setOnClickListener(radioButtonClickListener);
        rbWallet.setOnClickListener(radioButtonClickListener);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbUPI.isChecked()) {
                    // Initiate UPI payment
                    initiateUPIPayment(totalamount);
                } else {
                    Toast.makeText(Payment.this, "Please select UPI to proceed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Payment.this, Cart.class));
            }
        });

        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(Payment.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(Payment.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(Payment.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(Payment.this, Cart.class));
                    return true;
                }

                return false;
            }
        });
    }

    private void initiateUPIPayment(double amount) {
        // Construct UPI URI for payment
        Uri uri = Uri.parse("upi://pay?pa=receiver-upi-id@upi&pn=ReceiverName&mc=0000&tid=" + orderId + "&tn=Payment for Order " + orderId + "&am=" + amount + "&cu=INR");
        // Replace receiver-upi-id@upi with the actual UPI ID where the payment will be received
        Intent upiPaymentIntent = new Intent(Intent.ACTION_VIEW, uri);
        upiPaymentIntent.setPackage("com.google.android.apps.nbu.paisa.user"); // This will trigger Google Pay

        // Check if UPI app is available
        PackageManager packageManager = getPackageManager();
        if (packageManager.queryIntentActivities(upiPaymentIntent, 0).size() > 0) {
            startActivityForResult(upiPaymentIntent, UPI_PAYMENT_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No UPI app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPI_PAYMENT_REQUEST_CODE) {
            String response = data != null ? data.getStringExtra("response") : null;
            handleUPIPaymentResponse(response);
        }
    }

    private void handleUPIPaymentResponse(String response) {
        if (response == null) {
            Toast.makeText(this, "Payment failed or canceled", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] responseArr = response.split("&");
        String status = "";
        String paymentId = "";

        for (String s : responseArr) {
            if (s.startsWith("Status=")) {
                status = s.split("=")[1];
            }
            if (s.startsWith("txnId=")) {
                paymentId = s.split("=")[1];
            }
        }

        if ("SUCCESS".equalsIgnoreCase(status)) {
            Toast.makeText(this, "Payment Successful. Payment ID: " + paymentId, Toast.LENGTH_SHORT).show();

            // Update Firestore order document with payment status and payment ID
            updateOrderStatus(orderId, paymentId);

            // Create a new payment document in Firestore
            createPaymentRecord(paymentId, totalamount, orderId, sapid, "UPI");

            // Pass data to PaymentSuccess activity
            Intent successIntent = new Intent(Payment.this, PaymentSuccess.class);
            successIntent.putExtra("sapid", sapid);
            successIntent.putExtra("orderId", orderId);
            successIntent.putExtra("totalAmount", totalamount);
            successIntent.putExtra("paymentId", paymentId);
            startActivity(successIntent);

            // Close the current payment activity
            finish();
        } else {
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPaymentRecord(String paymentId, double amount, String orderId, String userId, String mode) {

        // Create a map for the payment document
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("amount", amount);  // Total amount
        paymentData.put("date", Timestamp.now());  // Payment date
        paymentData.put("mode", mode);  // Payment mode (UPI or Wallet)
        paymentData.put("order_id", orderId);  // Order ID
        paymentData.put("user_id", userId);  // SAP ID (user ID)
        paymentData.put("payment_status", "Paid");  // Payment status
        paymentData.put("payment_id", paymentId);

        // Reference to the "payment" collection
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference paymentRef = firestore.collection("payment").document(paymentId);

        // Add the payment record to Firestore
        paymentRef.set(paymentData)
                .addOnSuccessListener(aVoid -> {
                    // Optionally, handle success, like displaying a toast or logging
                    Log.d("Payment", "Payment record created successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle failure to add the payment record
                    Toast.makeText(Payment.this, "Error creating payment record", Toast.LENGTH_SHORT).show();
                });
    }


    private void updateOrderStatus(String orderId, String paymentId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference orderRef = firestore.collection("order").document(orderId);

        orderRef.update("status", "Placed", "payment_id", paymentId)
                .addOnSuccessListener(aVoid -> {
                    // Optionally, you can display a success message or redirect to another page
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Payment.this, "Error updating order status", Toast.LENGTH_SHORT).show();
                });
    }
}