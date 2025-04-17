package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WalletPin extends AppCompatActivity {

    private EditText[] newPin = new EditText[4];
    private EditText[] confirmPin = new EditText[4];
    private ImageView toggleNewPin, toggleConfirmPin;
    private boolean isNewPinVisible = false, isConfirmPinVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.wallet_pin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String sapid = getIntent().getStringExtra("sapid");

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WalletPin.this, VirtualWallet.class));
            }
        });

        // Bottom Navigation Bar Functionality
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bottomnav_home) {
                    startActivity(new Intent(WalletPin.this, HomePage.class));
                    return true;
                } else if (itemId == R.id.bottomnav_favorites) {
                    startActivity(new Intent(WalletPin.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.bottomnav_wallet) {
                    startActivity(new Intent(WalletPin.this, VirtualWallet.class));
                    return true;
                } else if (itemId == R.id.bottomnav_cart) {
                    startActivity(new Intent(WalletPin.this, Cart.class));
                    return true;
                }

                return false;
            }
        });


        ImageButton backbtn = findViewById(R.id.backButton);
        backbtn.setOnClickListener(view -> {
            Intent intent = new Intent(WalletPin.this, VirtualWallet.class);
            startActivity(intent);
        });

        newPin[0] = findViewById(R.id.newPin1);
        newPin[1] = findViewById(R.id.newPin2);
        newPin[2] = findViewById(R.id.newPin3);
        newPin[3] = findViewById(R.id.newPin4);

        confirmPin[0] = findViewById(R.id.confirmPin1);
        confirmPin[1] = findViewById(R.id.confirmPin2);
        confirmPin[2] = findViewById(R.id.confirmPin3);
        confirmPin[3] = findViewById(R.id.confirmPin4);

        //Set pin fields to be hidden by default
        setPinInputMode(newPin, false);
        setPinInputMode(confirmPin, false);

        // Auto-focus between pin fields
        setPinAutoFocus(newPin, confirmPin);
        setPinAutoFocus(confirmPin, null);

        toggleNewPin = findViewById(R.id.eyeToggleNewPin);
        toggleConfirmPin = findViewById(R.id.eyeToggleConfirmPin);
        Button submitpinbtn = findViewById(R.id.submitPinButton);

        toggleNewPin.setOnClickListener(v -> {
            isNewPinVisible = !isNewPinVisible;
            setPinInputMode(newPin, isNewPinVisible);
            toggleNewPin.setImageResource(isNewPinVisible ? R.drawable.eyeopen : R.drawable.eyeclose);
        });

        toggleConfirmPin.setOnClickListener(v -> {
            isConfirmPinVisible = !isConfirmPinVisible;
            setPinInputMode(confirmPin, isConfirmPinVisible);
            toggleConfirmPin.setImageResource(isConfirmPinVisible ? R.drawable.eyeopen : R.drawable.eyeclose);
        });

        submitpinbtn.setOnClickListener(v -> {
            String newPinText = getPinFromFields(newPin);
            String confirmPinText = getPinFromFields(confirmPin);

            if (newPinText.isEmpty() || confirmPinText.isEmpty()) {
                Toast.makeText(WalletPin.this, "Please enter all 4 digits in both fields", Toast.LENGTH_SHORT).show();
            } else if (!newPinText.equals(confirmPinText)) {
                Toast.makeText(WalletPin.this, "Pins do not match! Please try again", Toast.LENGTH_SHORT).show();
            } else {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("wallet_pin", Long.parseLong(newPinText)); // store as number

                db.collection("user").document(sapid).update(updateData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(WalletPin.this, "PIN created successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(WalletPin.this, WalletDisplay.class);
                            intent.putExtra("sapid", sapid);
                            startActivity(intent);
                            finish(); // optional: close current screen
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(WalletPin.this, "Failed to save PIN: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    // Functions
    // Auto Focus
    private void setPinAutoFocus(EditText[] pinFields, EditText[] nextFields) {
        for (int i = 0; i < pinFields.length; i++) {
            int nextIndex = i + 1;
            pinFields[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)}); // Allow only 1 digit

            pinFields[i].addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        if (nextIndex < pinFields.length) {
                            pinFields[nextIndex].requestFocus();
                        } else if (nextFields != null) {
                            nextFields[0].requestFocus(); // Move to confirm pin field after last newPin digit
                        }
                    }
                }

                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setPinInputMode(EditText[] pinFields, boolean isVisible) {
        int inputType = isVisible
                ? InputType.TYPE_CLASS_NUMBER
                : (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        for (EditText field : pinFields) {
            field.setInputType(inputType);
            field.setSelection(field.getText().length());
        }
    }

    private void togglePinVisibility(EditText[] pinFields, ImageView toggleButton) {
        boolean isVisible = pinFields[0].getInputType() == InputType.TYPE_CLASS_NUMBER;
        int inputType = isVisible
                ? (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                : InputType.TYPE_CLASS_NUMBER;

        for (EditText field : pinFields) {
            field.setInputType(inputType);
            field.setSelection(field.getText().length());
        }

        toggleButton.setImageResource(isVisible ? R.drawable.eyeclose : R.drawable.eyeopen);
    }

    private String getPinFromFields(EditText[] pinFields) {
        StringBuilder pin = new StringBuilder();
        for (EditText field : pinFields) {
            pin.append(field.getText().toString().trim());
        }
        return pin.toString();
    }
}