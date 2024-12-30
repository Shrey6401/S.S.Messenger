package com.example.ssmessenger.loginpages;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ssmessenger.databinding.ActivitySignupBinding;
import com.example.ssmessenger.model.User;
import com.example.ssmessenger.views.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yalantis.ucrop.UCrop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class signup extends AppCompatActivity {

    // Declare variables for view binding, permissions, and result handling
    ActivitySignupBinding signupBinding; // ViewBinding for accessing UI elements
    ActivityResultLauncher<String[]> permissionResultLauncher; // Launcher for permissions
    ArrayList<String> permissionsList = new ArrayList<>(); // List of required permissions
    ActivityResultLauncher<Intent> photoPickResultLauncher; // Launcher for photo picker
    ActivityResultLauncher<Intent> cropPhotoResultLauncher; // Launcher for photo crop
    Uri croppedImageUri; // URI of cropped profile image
    String userEmail, username, userPassword; // User credentials

    // Firebase instances for authentication and database
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users");
    String userUniqueId; // Unique ID of the user
    String profileImageUrl; // URL of the user's profile image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        signupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(signupBinding.getRoot());

        // Add necessary permissions based on API level
        if (Build.VERSION.SDK_INT > 33) {
            permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // Register activity result handlers
        registerActivityForMultiplePermissions();
        registerActivityForPhotoPicker();
        registerActivityForPhotoCrop();

        // Set up profile photo picker
        signupBinding.mainSignup.setOnClickListener(view -> {
            if (hasPermission()) {
                openPhotoPicker();
            } else {
                shouldShowPermissionRationaleIfNeeded();
            }
        });

        // Set up signup button functionality
        signupBinding.buttonSignup.setOnClickListener(v -> createNewUser());
    }

    /**
     * Creates a new user account and saves the user's data in Firebase.
     */
    public void createNewUser() {
        // Retrieve user inputs
        username = signupBinding.editusername.getText().toString().trim();
        userEmail = signupBinding.editEmailsignup.getText().toString().trim();
        userPassword = signupBinding.editPasswordSignup.getText().toString().trim();

        // Check if fields are empty
        if (username.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Username, email address, and password cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            signupBinding.buttonSignup.setEnabled(false);
            signupBinding.Progressbarsignup.setVisibility(View.VISIBLE);

            // Create user with Firebase Authentication
            auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userUniqueId = auth.getCurrentUser().getUid();
                    saveUserInfoToDatabase();
                } else {
                    // Handle errors
                    Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    signupBinding.buttonSignup.setEnabled(true);
                    signupBinding.Progressbarsignup.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    /**
     * Saves user information to Firebase Realtime Database.
     */
    public void saveUserInfoToDatabase() {
        // Create a User object with input data
        User user = new User(userUniqueId, username, userEmail, profileImageUrl);

        // Save user data to the database
        databaseReference.child(userUniqueId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Your account has been successfully created", Toast.LENGTH_SHORT).show();
                // Navigate to main activity
                Intent intent = new Intent(signup.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Handle errors
                Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            // Reset UI
            signupBinding.buttonSignup.setEnabled(true);
            signupBinding.Progressbarsignup.setVisibility(View.INVISIBLE);
        });
    }

    /**
     * Registers the activity for handling multiple permission results.
     */
    public void registerActivityForMultiplePermissions() {
        permissionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (!entry.getValue()) {
                            allGranted = false;
                            break;
                        }
                    }

                    if (allGranted) {
                        openPhotoPicker();
                    } else {
                        shouldShowPermissionRationaleIfNeeded();
                    }
                }
        );
    }

    /**
     * Opens the photo picker for selecting a profile image.
     */
    public void openPhotoPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickResultLauncher.launch(intent);
    }

    /**
     * Registers the activity for handling photo picker results.
     */
    public void registerActivityForPhotoPicker() {
        photoPickResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uncroppedImageUri = result.getData().getData();
                        cropSelectedImage(uncroppedImageUri);
                    }
                }
        );
    }

    /**
     * Registers the activity for handling photo cropping results.
     */
    public void registerActivityForPhotoCrop() {
        cropPhotoResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                cropResult -> {
                    if (cropResult.getResultCode() == RESULT_OK && cropResult.getData() != null) {
                        croppedImageUri = UCrop.getOutput(cropResult.getData());
                        if (croppedImageUri != null) {
                            Picasso.get().load(croppedImageUri).into(signupBinding.profilesignup);
                        }
                    }
                }
        );
    }

    /**
     * Crops the selected image using UCrop library.
     */
    public void cropSelectedImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped" + System.currentTimeMillis()));
        Intent cropIntent = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .getIntent(signup.this);
        cropPhotoResultLauncher.launch(cropIntent);
    }

    /**
     * Checks and shows permission rationale if necessary.
     */
    public void shouldShowPermissionRationaleIfNeeded() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissionsList) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.isEmpty()) {
            Snackbar.make(signupBinding.mainSignup, "Please grant necessary permissions to set a profile photo", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", view -> permissionResultLauncher.launch(permissionsList.toArray(new String[0])))
                    .show();
        } else {
            permissionResultLauncher.launch(deniedPermissions.toArray(new String[0]));
        }
    }

    /**
     * Checks if all required permissions have been granted.
     */
    public boolean hasPermission() {
        for (String permission : permissionsList) {
            if (ContextCompat.checkSelfPermission(this, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
