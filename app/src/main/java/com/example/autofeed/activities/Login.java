package com.example.autofeed.activities;

import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autofeed.R;
import com.example.autofeed.fragments.Home;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private FirebaseAuth auth;          //authentication
    private FirebaseDatabase rootNode;  //real time database
    private DatabaseReference reference;

    //Define var
    private TextInputEditText username, password;
    private Button login_btn;
    private TextView newUser;

    @Override
    public void onStart() {
        super.onStart();
        int ActivityKey = getIntent().getIntExtra("ActivityKey", 0);
        Log.d(TAG, "onStart, ActivityKey: " + ActivityKey);
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser != null) {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_login);

        setVariables();


        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
//        rootNode = FirebaseDatabase.getInstance();

        //var.setText(###) -> var = ###

        //var.setOnClickListener(view -> function())
        //wait for button being pressed then execute function()
        login_btn.setOnClickListener(view -> login());
        newUser.setOnClickListener(view -> createNewUser());
    }

    public void setVariables() {
        username = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        login_btn = findViewById(R.id.btnLogin);
        newUser = findViewById(R.id.tvNewuser);
    }

    public void login() {
        Log.d(TAG, "login");
        String userEmail = Objects.requireNonNull(username.getText()).toString();
        String userPassword = Objects.requireNonNull(password.getText()).toString();

        //readFireBase(userEmail);
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                       // makeText(Login.this, "Login Succesaful", Toast.LENGTH_SHORT).show();

                        //pass user name for later use
                        readFireBase(userEmail);
                        startActivity(new Intent(this, MainPage.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void readFireBase(String username) {
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String name = Objects.requireNonNull(dataSnapshot.child(username).child("email").getValue()).toString();
                String name = Objects.requireNonNull(dataSnapshot.child(encodeUserEmail(username)).child("User Info").child("name").getValue()).toString();
                //makeText(Login.this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Value is: " + name);
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

    public void createNewUser() {
        startActivity(new Intent(this, NewUser.class));
        finish();
    }

}