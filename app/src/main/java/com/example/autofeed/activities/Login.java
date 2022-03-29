package com.example.autofeed.activities;

// define libraries

import static android.widget.Toast.makeText;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.autofeed.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

public class Login extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth auth;          // define variable for FireBase authentication
    private DatabaseReference reference;// define variable for access realtime database

    private TextInputEditText username, password;// define variables for text input
    private Button login_btn;                    // define variable for button
    private TextView newUser;                    // define variable for text


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        setVariables();// function for variables setup

        auth = FirebaseAuth.getInstance();         // get access for FireBase authentication
        reference = FirebaseDatabase.getInstance() // access branch in realtime database "Users"
                .getReference().child("Users");    //


        login_btn.setOnClickListener(view -> login());      // if login_btn was pressed go to login() function
        newUser.setOnClickListener(view -> createNewUser());// if newUser was pressed go to createNewUser() function
    }

    public void setVariables() {                 // function for linking the UI to the code
        username = findViewById(R.id.etEmail);   //
        password = findViewById(R.id.etPassword);//
        login_btn = findViewById(R.id.btnLogin); //
        newUser = findViewById(R.id.tvNewuser);  //
    }

    public void login() {                                               //function for setup the login process
        Log.d(TAG, "login");
        String userEmail = Objects.requireNonNull(username.getText()).toString();   //variable to hold the user's email
        String userPassword = Objects.requireNonNull(password.getText()).toString();//variable to hold the user's password

        //readFireBase(userEmail);
        auth.signInWithEmailAndPassword(userEmail, userPassword)//verify if the user's credentials exist
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        // makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        //pass user name for later use
                        startActivity(new Intent(this, MainPage.class));// transfer user to the app
                        finish();                                                    // finish this activity
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void createNewUser() { // function that send the user to another activity for signing up
        startActivity(new Intent(this, NewUser.class));// starts NewUser activity
        finish();                                                    // finish current activity
    }

}