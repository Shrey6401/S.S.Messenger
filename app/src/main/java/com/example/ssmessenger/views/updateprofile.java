package com.example.ssmessenger.views;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ssmessenger.R;
import com.example.ssmessenger.databinding.ActivityUpdateprofileBinding;
import com.example.ssmessenger.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class updateprofile extends AppCompatActivity {
    ActivityUpdateprofileBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser CurrentUser;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users");
    String userName;
    String userId;
    String userEmail;
    String imageUrl;
    ValueEventListener updateValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        CurrentUser = auth.getCurrentUser();
        getAndShowUserInfo();
        binding.toolbarUpdateProfile.setOnMenuItemClickListener(v -> {
            finish();
            return false;
        });

    }
    public void getAndShowUserInfo(){
        if (CurrentUser != null){
            updateValueEventListener = databaseReference.child(CurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null){
                        userName = user.getUserName();
                        userId = user.getUserId();
                        userEmail = user.getUserEmail();
                        imageUrl = user.getImageUrl();

                        binding.editusernameUpdateProfile.setText(userName);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(updateprofile.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (updateValueEventListener != null){
            databaseReference.child(CurrentUser.getUid()).removeEventListener(updateValueEventListener);

        }
    }
}