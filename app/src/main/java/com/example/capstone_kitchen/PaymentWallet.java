package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PaymentWallet extends AppCompatActivity {

    EditText pin1, pin2, pin3, pin4;
    private RadioButton rbUPI, rbWallet;
    private boolean isPinVisible = false;
    private ImageButton togglePinVisibility;
    Button btnPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_wallet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        togglePinVisibility = findViewById(R.id.togglePinVisibility);

        // Wallet PIN
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


        // Toggle PIN Visibility
        togglePinVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPinVisible) {
                    // Hide PIN
                    setPinInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    togglePinVisibility.setImageResource(R.drawable.hidewalletpin);
                } else {
                    // Show PIN
                    setPinInputType(InputType.TYPE_CLASS_NUMBER);
                    togglePinVisibility.setImageResource(R.drawable.showwalletpin);
                }
                isPinVisible = !isPinVisible;
            }
        });


        // Check if wallet was previously selected
        boolean walletSelected = getIntent().getBooleanExtra("walletSelected", false);
        if (walletSelected) {
            rbWallet.setChecked(true);
            rbUPI.setChecked(false);
        }

        // Custom RadioGroup behavior
        View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == rbUPI) {
                    rbUPI.setChecked(true);
                    rbWallet.setChecked(false);

                    // Intent to navigate to the previous page when UPI is selected
                    Intent intent = new Intent(PaymentWallet.this, Payment.class);
                    startActivity(intent);

                } else if (v == rbWallet) {
                    rbWallet.setChecked(true);
                    rbUPI.setChecked(false);
                }
            }
        };

        rbUPI.setOnClickListener(radioButtonClickListener);
        rbWallet.setOnClickListener(radioButtonClickListener);


        // Place Order Button Functionality
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!rbUPI.isChecked() && !rbWallet.isChecked()) {
                    Toast.makeText(PaymentWallet.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                } else if (rbWallet.isChecked()) {
                    String pin = pin1.getText().toString() + pin2.getText().toString() + pin3.getText().toString() + pin4.getText().toString();
                    if (pin.length() == 4 && pin.equals("1234")) {
                        // Navigate to Payment Success Page
                        Intent intent = new Intent(PaymentWallet.this, PaymentSuccess.class);
                        startActivity(intent);
                        finish(); // Optional: Close current activity
                    } else {
                        Toast.makeText(PaymentWallet.this, "Please enter a valid 4-digit PIN", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(PaymentWallet.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(PaymentWallet.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(PaymentWallet.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(PaymentWallet.this, Payment.class));
                    return true;
                }

                return false;
            }
        });


    }

    // Function to update PIN visibility for all fields
    private void setPinInputType(int inputType) {
        pin1.setInputType(inputType);
        pin2.setInputType(inputType);
        pin3.setInputType(inputType);
        pin4.setInputType(inputType);

        // Maintain cursor position
        pin1.setSelection(pin1.getText().length());
        pin2.setSelection(pin2.getText().length());
        pin3.setSelection(pin3.getText().length());
        pin4.setSelection(pin4.getText().length());
    }

    // Function to move cursor to the next box when input is entered
    private void moveCursorOnInput(EditText current, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Function to handle backspace: Moves cursor back when deleting
    private void moveCursorOnDelete(EditText current, EditText previous) {
        current.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && current.getText().toString().isEmpty()) {
                previous.requestFocus();
            }
            return false;
        });
    }
}