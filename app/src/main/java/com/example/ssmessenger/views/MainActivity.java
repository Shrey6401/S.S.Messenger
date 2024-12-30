package com.example.ssmessenger.views;

// Import necessary classes and packages
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.ssmessenger.R;
import com.example.ssmessenger.databinding.ActivityLoginBinding;
import com.example.ssmessenger.databinding.ActivityMainBinding;
import com.example.ssmessenger.loginpages.login;
import com.example.ssmessenger.loginpages.signup;
import com.google.firebase.auth.FirebaseAuth;

// MainActivity class that extends AppCompatActivity
public class MainActivity extends AppCompatActivity {

    // Declare variables for UI components and Firebase authentication
    Button loginsignup;
    ActivityLoginBinding loginBinding; // Binding for the login activity
    ActivityMainBinding activityMainBinding; // Binding for the main activity
    FirebaseAuth auth = FirebaseAuth.getInstance(); // Firebase authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view bindings for the activities
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        // Set the content view to the login binding's root view
        setContentView(loginBinding.getRoot());

        // Initialize Firebase authentication instance
        auth = FirebaseAuth.getInstance();

        // Reference the login/signup button using its ID
        loginsignup = findViewById(R.id.buttonLogSignup);

        // Set an OnClickListener for the login/signup button
        loginsignup.setOnClickListener(v -> {
            // Navigate to the signup activity
            Intent intent = new Intent(MainActivity.this, signup.class);
            startActivity(intent);
            finish(); // Finish the current activity
        });

        // Customize the toolbar overflow icon
        activityMainBinding.toolbarmain.setOverflowIcon(
                AppCompatResources.getDrawable(this, R.drawable.more)
        );

        // Set a listener for toolbar menu item clicks
        activityMainBinding.toolbarmain.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.editprfileitem) {
                // Navigate to the update profile activity if "Edit Profile" is clicked
                Intent intent = new Intent(MainActivity.this, updateprofile.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.signoutitem) {
                // Sign out the user and navigate to the login activity if "Sign Out" is clicked
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish(); // Finish the current activity
                return true;
            } else {
                return false; // Return false for unhandled menu items
            }
        });
    }
}
