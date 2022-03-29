package com.example.autofeed.activities;

import static android.widget.Toast.makeText;
import android.content.Intent;
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

    private FirebaseAuth auth;          // define variable for FireBase authentication
    private DatabaseReference reference;// define variable for access realtime database


    private EditText userName, userEmail, userPassword, confirmPassword; // define variables for text input
    private Button confirmBtn;                                           // define variable for button


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        auth = FirebaseAuth.getInstance();                                    // get access for FireBase authentication
        reference = FirebaseDatabase.getInstance().getReference("Users");// access branch in realtime database "Users"

        setVariables(); // execute function for linking the UI to the code

        confirmBtn.setOnClickListener(view -> credentials()); // if confirmBtn was pressed execute credentials() function

    }

    private void setVariables() {                              //function for linking the UI to the code
        userName = findViewById(R.id.etName);                  //
        userEmail = findViewById(R.id.etEmail);                //
        userPassword = findViewById(R.id.etPassword);          //
        confirmPassword = findViewById(R.id.etConfirmPassword);//
        confirmBtn = findViewById(R.id.btnConfirm);            //

    }

    public void credentials() {
        String user_name = userName.getText().toString().trim();        //variable to hold the user's name
        String user_mail = userEmail.getText().toString().trim();       //variable to hold the user's email
        String user_password = userPassword.getText().toString().trim();//variable to hold the user's password

        if (user_mail.isEmpty() && user_password.isEmpty()) {           //check for missing credentials and alert the user
            makeText(NewUser.this, "Enter all missing details", Toast.LENGTH_SHORT).show();
        } else if (user_password.isEmpty())
            makeText(NewUser.this, "Enter Email", Toast.LENGTH_SHORT).show();
        else if (user_mail.isEmpty())
            makeText(NewUser.this, "Enter Password", Toast.LENGTH_SHORT).show();
        else {
            auth.fetchSignInMethodsForEmail(user_mail).addOnCompleteListener(task -> {//if all credentials filled create check if email exist

                if (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).size() == 0) {// if email is not in the database then create new user
                    makeText(NewUser.this, "New user", Toast.LENGTH_SHORT).show(); // pop up message for success creating new user
                    setUser(user_mail, user_password, user_name);// execute a function to create user credentials in FireBase
                } else {
                    makeText(NewUser.this, "Existed user", Toast.LENGTH_SHORT).show(); //email already exist, send  pop up message to the user
                }
            });
        }
    }


    public void setUser(String user_mail, String user_password, String user_name) {                      // function for creating new user
        //upload to FireBase
        auth.createUserWithEmailAndPassword(user_mail, user_password)                                    // creating new user in FireBase Authentication
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful()) {                                                           // if succeeded to Create new user
                        Log.d(TAG, "Registration successful");
                        makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();// pop up message for success registration
                        saveDateFireBase(user_mail, user_password, user_name);                            // execute a function for saving user credentials in realtime database


                        startActivity(new Intent(getApplicationContext(), Login.class)//send the user back to the Login activity
                                .putExtra("ActivityKey", 1));
                        Log.d(TAG, "After startActivity");
                        finish(); // finish current activity
                    } else {
                        Log.d(TAG, "Registration failed");
                        makeText(this, "Registration failed", Toast.LENGTH_SHORT).show(); // pop up message for failed registration
                    }
                });
    }

    private void saveDateFireBase(String email, String password, String name) { // function for storing user credentials in realtime database

        User user = new User(email, password, name); //create a class to store  user credentials
        reference.child(Objects.requireNonNull((encodeUserEmail(email)))).child("User Info").setValue(user); //uploading  user credentials to firebase
    }

    @Override
    public void onBackPressed() {                                   // on back press return to login activity
        super.onBackPressed();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    static String encodeUserEmail(String userEmail) {  // function that replace '.' with ',' in the user's email
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