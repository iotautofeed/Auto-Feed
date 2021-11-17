package com.example.autofeed.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.autofeed.R;
import com.example.autofeed.classes.PetInfo;
import com.example.autofeed.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserInfo extends AppCompatActivity {

    private static final String TAG = "TAG";
    private FirebaseAuth auth;          //authentication
    private FirebaseDatabase rootNode;  //real time database
    private DatabaseReference reference;

    private TextView username, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        setVariables();
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()))).child("User Info");
        readFireBase();
    }

    private void setVariables() {
        username = findViewById(R.id.tvUserName);
        userEmail = findViewById(R.id.tvUserEmail);

    }

    private void readFireBase() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    username.setText(user.getName());
                    userEmail.setText(user.getEmail());
                } else {
                    User userTemp = new User();
                    username.setText(userTemp.getName());
                    userEmail.setText(userTemp.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}