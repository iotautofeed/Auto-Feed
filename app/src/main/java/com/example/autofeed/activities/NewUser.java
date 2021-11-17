package com.example.autofeed.activities;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autofeed.R;
import com.example.autofeed.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class NewUser extends AppCompatActivity {

    private static final String TAG = "NewUser";

    private FirebaseAuth auth;          //authentication
    private FirebaseDatabase rootNode;  //real time database
    private DatabaseReference reference;

    //Define var
    private EditText userName, userEmail, userPassword, confirmPassword;
    private Button confirmBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);


        auth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Users");

        setVariables();


        confirmBtn.setOnClickListener(view -> checkEmail());

    }

    private void setVariables() {
        userName = findViewById(R.id.etName);
        userEmail = findViewById(R.id.etEmail);
        userPassword = findViewById(R.id.etPassword);
        confirmPassword = findViewById(R.id.etConfirmPassword);
        confirmBtn = findViewById(R.id.btnConfirm);

    }

//    public void checkUser() {
//        String user_name = userName.getText().toString().trim();
//        String user_mail = userEmail.getText().toString();
//        String user_password = userPassword.getText().toString();
//
//        if (validateEmail(user_mail) && validateName(user_name) && validatePassword(user_password)) {
//            saveDateFireBase(user_mail, user_password, user_name);
//            startActivity(new Intent(getApplicationContext(), Login.class)
//                    .putExtra("ActivityKey", 1));
//            Log.d(TAG, "After startActivity");
//            finish();
//        } else {
//            Log.d(TAG, "Registration failed");
//            makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    private Boolean validateName(String user_name) {
//
//        String noWhiteSpace = "\\A\\w{4,20}\\z";
//
//
//        if (user_name.isEmpty()) {
//            userName.setError("Field cannot be empty");
//            return false;
//        } else if (!user_name.matches(noWhiteSpace)) {
//            userName.setError("White Spaces are not allowed");
//            return false;
//        } else {
//            userName.setError(null);
//            return true;
//        }
//    }
//
//    private Boolean validateEmail(String user_mail) {
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//
//        if (user_mail.isEmpty()) {
//            userEmail.setError("Field cannot be empty");
//            return false;
//        } else if (!user_mail.matches(emailPattern)) {
//            userEmail.setError("Invalid email address");
//            return false;
//        } else {
//            userEmail.setError(null);
//            return true;
//        }
//    }
//
//    private Boolean validatePassword(String user_password) {
//        String passwordVal = "^" +
//                //"(?=.*[0-9])" +         //at least 1 digit
//                //"(?=.*[a-z])" +         //at least 1 lower case letter
//                //"(?=.*[A-Z])" +         //at least 1 upper case letter
//                //"(?=.*[a-zA-Z])" +      //any letter
//                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
//                "(?=\\S+$)" +           //no white spaces
//                ".{6,}" +               //at least 4 characters
//                "$";
//
//        if (user_password.isEmpty()) {
//            userPassword.setError("Field cannot be empty");
//            return false;
//        } else if (!user_password.matches(passwordVal)) {
//            userPassword.setError("Password is too weak");
//            return false;
//        } else {
//            userPassword.setError(null);
//            return true;
//        }
//    }

    private void saveDateFireBase(String email, String password, String name) {

        User user = new User(email, password, name);
        reference.child(Objects.requireNonNull((encodeUserEmail(email)))).child("User Info").setValue(user);
    }

//
    public void setUser(String user_mail, String user_password, String user_name) {
        //upload to FireBase
        auth.createUserWithEmailAndPassword(user_mail, user_password)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Registration successful");
                        makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                        saveDateFireBase(user_mail, user_password, user_name);


                        startActivity(new Intent(getApplicationContext(), Login.class)
                                .putExtra("ActivityKey", 1));
                        Log.d(TAG, "After startActivity");
                        finish();
                    } else {
                        Log.d(TAG, "Registration failed");
                        makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void checkEmail() {
        String user_name = userName.getText().toString().trim();
        String user_mail = userEmail.getText().toString().trim();
        String user_password = userPassword.getText().toString().trim();

        if (user_mail.isEmpty() && user_password.isEmpty()) {
            makeText(NewUser.this, "Enter all missing details", Toast.LENGTH_SHORT).show();
        } else if (user_password.isEmpty())
            makeText(NewUser.this, "Enter Email", Toast.LENGTH_SHORT).show();
        else if (user_mail.isEmpty())
            makeText(NewUser.this, "Enter Password", Toast.LENGTH_SHORT).show();
        else {
            auth.fetchSignInMethodsForEmail(user_mail).addOnCompleteListener(task -> {
                //Log.d(TAG, "" + task.getResult().getSignInMethods().size());
                if (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).size() == 0) {
                    makeText(NewUser.this, "New user", Toast.LENGTH_SHORT).show();
                    setUser(user_mail, user_password, user_name);
                } else {
                    makeText(NewUser.this, "Existed user", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}


//auth.getCurrentUid();

//    public Boolean chekPassword() {
//        String user_password = userPassword.getText().toString().trim();
//        String confirmPass = confirmPassword.getText().toString().trim();
//
//        if (user_password.length() < 21 && userPassword.length() > 5) {
//            if (user_password.equals(confirmPass)) {
//                return true;
//            }
//        }
//        return false;
//    }
