package com.example.capstone_kitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {

    private EditText username, password;
    private ImageView showPasswordIcon;
    private Button loginButton;
    private TextView forgotPassword, signupText;
    private boolean isPasswordVisible = false;  // Track visibility state

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

        // 👁️ Toggle password visibility
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

        // 🔐 Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();

                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(activity_login_page.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Redirect to HomePage
                    Intent intent = new Intent(activity_login_page.this, HomePage.class);
                    startActivity(intent);
                    finish(); // Close login page
                }
            }
        });

        // 📌 Forgot password click listener
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity_login_page.this, "Forgot Password Clicked!", Toast.LENGTH_SHORT).show();
                // Add logic to open forgot password page
            }
        });

        // 🔹 Sign Up click listener
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_login_page.this, SignupPage.class);
                startActivity(intent);
            }
        });
    }
}
