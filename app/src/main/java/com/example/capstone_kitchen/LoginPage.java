package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginPage extends AppCompatActivity {

    private EditText username, password;
    private ImageView showPasswordIcon;
    private Button loginButton;
    private TextView forgotPassword, signupText;
    private boolean isPasswordVisible = false;  // Track visibility state
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize UI components
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        showPasswordIcon = findViewById(R.id.showPasswordIcon);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        signupText = findViewById(R.id.signupText);

        db = FirebaseFirestore.getInstance();

        // Toggle password visibility
        showPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPasswordIcon.setImageResource(R.drawable.pswd_eye);  // Closed eye icon
                } else {
                    // Show password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPasswordIcon.setImageResource(R.drawable.pswd_eye);  // Open eye icon
                }
                isPasswordVisible = !isPasswordVisible; // Toggle state
                password.setSelection(password.getText().length()); // Move cursor to end
            }
        });

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();

                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(LoginPage.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Verify username and password with Firestore
                    verifyLoginCredentials(enteredUsername, enteredPassword);
                }
            }
        });

        // Forgot password click listener
        // Forgot password click listener
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = username.getText().toString().trim();

                if (enteredUsername.isEmpty()) {
                    // If the username is empty, prompt the user to enter it
                    Toast.makeText(LoginPage.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the entered username exists in Firestore
                    db.collection("user")  // Reference to the "user" collection in Firestore
                            .whereEqualTo("username", enteredUsername)  // Search for the entered username
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot documentSnapshots = task.getResult();
                                    if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                                        // If the username exists
                                        for (QueryDocumentSnapshot document : documentSnapshots) {
                                            // Get the document ID (SAPID) and pass it to CreatePasswordActivity
                                            String sapid = document.getId();  // Firestore document ID
                                            Intent intent = new Intent(LoginPage.this, CreatePasswordActivity.class);
                                            intent.putExtra("sapid", sapid);  // Pass SAPID to the next activity
                                            startActivity(intent);
                                        }
                                    } else {
                                        // If username is not found in the database
                                        Toast.makeText(LoginPage.this, "Username not found. Please sign up first.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Firestore query failed
                                    Toast.makeText(LoginPage.this, "Error checking username. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Sign Up click listener
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignupPage.class);
                startActivity(intent);
            }
        });
    }

    // Method to verify login credentials with Firestore
    private void verifyLoginCredentials(String username, String password) {
        db.collection("user")  // Reference to "user" collection in Firestore
                .whereEqualTo("username", username)  // Search for the entered username
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                            // If username exists
                            for (QueryDocumentSnapshot document : documentSnapshots) {
                                // Assuming 'username' and 'password' are the field names in the document
                                String storedPassword = document.getString("password");
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    // Password matches
                                    String sapid = document.getId();

                                    Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginPage.this, HomePage.class);
                                    intent.putExtra("username", username);  // Passing username to HomePage
                                    intent.putExtra("sapid", sapid);
                                    startActivity(intent);
                                    finish();  // Close login page
                                } else {
                                    // Password doesn't match
                                    Toast.makeText(LoginPage.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // Username does not exist
                            Toast.makeText(LoginPage.this, "Account/Username doesn't exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Firestore query failed
                        Log.d("Login", "Error getting documents: ", task.getException());
                        Toast.makeText(LoginPage.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}