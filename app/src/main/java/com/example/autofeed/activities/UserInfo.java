package com.example.autofeed.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.autofeed.R;
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
    private FirebaseAuth auth;            // define variable for FireBase authentication
    private DatabaseReference reference;  // define variable for access realtime database
    private TextView username, userEmail; // define variable for text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()))).child("User Info");

        setVariables(); // execute function for linking the UI to the code
        readFireBase(); // execute function that read data from FireBase
    }

    private void setVariables() {                   // function for linking the UI to the code
        username = findViewById(R.id.tvUserName);   //
        userEmail = findViewById(R.id.tvUserEmail); //
    }

    private void readFireBase() {                                          // function that read data from FireBase
        reference.addValueEventListener(new ValueEventListener() {         //  listen to the branch reference is set and retrieve data from it
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //
                User user = dataSnapshot.getValue(User.class);             // create new User class for user data
                if (user != null) {                                        // if user is not empty
                    username.setText(user.getName());                      // display user's name
                    userEmail.setText(user.getEmail());                    // display user's email
                } else {                                                   // otherwise
                    User userTemp = new User();                            // create default User class
                    username.setText(userTemp.getName());                  // display user's name
                    userEmail.setText(userTemp.getEmail());                // display user's email
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