package com.example.ssmessenger.loginpages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ssmessenger.views.MainActivity;
import com.example.ssmessenger.R;
import com.example.ssmessenger.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    // Declare FirebaseAuth and ViewBinding
    ActivityLoginBinding loginBinding; // ViewBinding for accessing views without findViewById
    FirebaseAuth auth; // Firebase authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d("FirebaseAuth", "User already logged in: " + user.getEmail());
            navigateToMainActivity();
        }

        // Adjust padding for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle Login Button Click
        loginBinding.buttonLogin.setOnClickListener(v -> {
            Log.d("LoginButton", "Login button clicked!");
            handleLogin();
        });

        // Handle Sign-Up Button Click
        loginBinding.buttonLogSignup.setOnClickListener(v -> {
            Log.d("SignUpButton", "Sign-Up button clicked!");
            navigateToSignUpActivity();
        });

        // Handle Text View Sign-Up Click
        loginBinding.textviewsignup.setOnClickListener(v -> navigateToSignUpActivity());
    }

    /**
     * Handles the login process by validating input and authenticating with Firebase.
     */
    private void handleLogin() {
        // Retrieve user input for email and password
        String email = loginBinding.editEmaillogin.getText().toString().trim();
        String password = loginBinding.editPasswordlogin.getText().toString().trim();

        // Check if email or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("FirebaseAuth", "Attempting login with email: " + email);

            // Attempt to sign in with Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseAuth", "Login successful. Navigating to MainActivity.");
                            navigateToMainActivity();
                        } else {
                            Log.e("FirebaseAuth", "Login failed: " + task.getException().getMessage());
                            Toast.makeText(
                                    login.this,
                                    "Login failed: " + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        }
    }

    /*
     * Navigates the user to the main activity after successful login.
     */
    private void navigateToMainActivity() {
        Log.d("Navigation", "Navigating to MainActivity");
        Intent intent = new Intent(login.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

    /*
     * Navigates the user to the sign-up activity for account registration.
     */
    private void navigateToSignUpActivity() {
        Log.d("Navigation", "Navigating to SignUp Activity");
        Intent intent = new Intent(login.this, signup.class);
        startActivity(intent);
    }
}
