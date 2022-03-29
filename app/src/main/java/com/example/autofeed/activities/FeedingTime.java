package com.example.autofeed.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.autofeed.R;
import com.example.autofeed.classes.FeedSchedule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeedingTime extends AppCompatActivity {
    private static final String TAG = "TAG";
    private TextView feedTime1, feedTime2, feedTime3, feedTime4; //define text variables
    private TextView portion1, portion2, portion3, portion4;     //define text variables
    private SwitchCompat sw1, sw2, sw3, sw4;                     //define switch variables
    private FirebaseAuth auth;                                   // define FireBase authentication variable
    private DatabaseReference reference, switch_ref, schedule_ref, currentPet_ref; //define access realtime database variable
    private TextView[] scheduleTime, schedulePortion;            //define an array of textviews variable
    private Button remove_btn;                                   //define text variables
    private int currentPet, numOfFeeds;                          // define int variable
    private List<Integer> hour = new ArrayList<>();               // define dynamic list variable
    private List<Integer> minute = new ArrayList<>();            // define dynamic list variable
    private List<Integer> food = new ArrayList<>();              // define dynamic list variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding_time);

        auth = FirebaseAuth.getInstance();                                            // get access for FireBase authentication

        reference = FirebaseDatabase.getInstance().getReference("Users")       //
                .child(encodeUserEmail(                                             // access branch in realtime database :
                        Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser())// "Users/(user's email)/Feed Time"
                                .getEmail()))).child(("Feed Time"));                        //

        currentPet_ref = FirebaseDatabase.getInstance().getReference("Pets"); // access branch in realtime database : "Pes"
        switch_ref = FirebaseDatabase.getInstance().getReference("switch"); //  access branch in realtime database : "switch"
        schedule_ref = reference.child("schedule");     // access branch in realtime database : "(reference)/Feed_Time"

        setVariables();   // execute function for linking the UI to the code
        getDataFireBase();  // execute function for reading data from firebase


        scheduleTime = new TextView[]{feedTime1, feedTime2, feedTime3, feedTime4}; // initialize the array
        schedulePortion = new TextView[]{portion1, portion2, portion3, portion4};  // initialize the array

        sw1.setOnClickListener(v -> setSwitch1()); // if sw1 was pressed execute setSwitch1() function
        sw2.setOnClickListener(v -> setSwitch2()); // if sw2 was pressed execute setSwitch2() function
        sw3.setOnClickListener(v -> setSwitch3()); // if sw3 was pressed execute setSwitch3() function
        sw4.setOnClickListener(v -> setSwitch4()); // if sw4 was pressed execute setSwitch4() function
        remove_btn.setOnClickListener(v -> removeFromSchedule()); // if remove_btn was pressed execute removeFromSchedule() function

    }

    private void setVariables() {                   // function for linking the UI to the code
        feedTime1 = findViewById(R.id.tvFeedTime1); //
        feedTime2 = findViewById(R.id.tvFeedTime2); //
        feedTime3 = findViewById(R.id.tvFeedTime3); //
        feedTime4 = findViewById(R.id.tvFeedTime4); //
        portion1 = findViewById(R.id.tvPortion1);   //
        portion2 = findViewById(R.id.tvPortion2);   //
        portion3 = findViewById(R.id.tvPortion3);   //
        portion4 = findViewById(R.id.tvPortion4);   //
        sw1 = findViewById(R.id.switchButton1);     //
        sw2 = findViewById(R.id.switchButton2);     //
        sw3 = findViewById(R.id.switchButton3);     //
        sw4 = findViewById(R.id.switchButton4);     //
        remove_btn = findViewById(R.id.btnRemove);  //
    }

    private void setSwitch1() {                 // function for handle switch state and update firebase

        if (sw1.isChecked()) {                  // if switch checked
            sw1.setChecked(true);
            switch_ref.child("sw1").setValue(1); // set value in branch : "(reference) / sw1"
        } else {                                // otherwise
            switch_ref.child("sw1").setValue(0); // set value in branch : "(reference) / sw1"
        }
    }

    private void setSwitch2() {                 // function for handle switch state and update firebase
        if (sw2.isChecked()) {                  // if switch checked
            sw2.setChecked(true);
            switch_ref.child("sw2").setValue(1); // set value in branch : "(reference) / sw2"
        } else {                                // otherwise
            switch_ref.child("sw2").setValue(0); // set value in branch : "(reference) / sw2"
        }
    }

    private void setSwitch3() {                 // function for handle switch state and update firebase
        if (sw3.isChecked()) {                  // if switch checked
            sw3.setChecked(true);
            switch_ref.child("sw3").setValue(1); // set value in branch : "(reference) / sw3"
        } else {                                // otherwise
            switch_ref.child("sw3").setValue(0); // set value in branch : "(reference) / sw3"
        }
    }

    private void setSwitch4() {                 // function for handle switch state and update firebase
        if (sw4.isChecked()) {                  // if switch checked
            sw4.setChecked(true);
            switch_ref.child("sw4").setValue(1); // set value in branch : "(reference) / sw4"
        } else {                                // otherwise
            switch_ref.child("sw4").setValue(0); // set value in branch : "(reference) / sw4"
        }
    }

    private void getDataFireBase() {
        currentPet_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentPet = Integer.parseInt(Objects.requireNonNull(snapshot.child("Current Pet").getValue()).toString());
                currentPet++;
                Log.d(TAG, "Current Pet: " + currentPet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        schedule_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hour.clear();  // clear list
                minute.clear();// clear list
                food.clear();  // clear list
                for (DataSnapshot ds : snapshot.child(String.valueOf(currentPet)).getChildren()) { // get all pets id of the current user
                    if (ds.exists()) { // if branch in firebase exist
                        hour.add(ds.child("hour").getValue(Integer.class));    // add the value in "(ds)/hour" to hour
                        minute.add(ds.child("minute").getValue(Integer.class)); // add the value in "(ds)/minute" to minute
                        food.add(ds.child("food").getValue(Integer.class));    // add the value in "(ds)/food" to food
                    }
                }
                numOfFeeds = hour.size(); // set num of feeding schedules

                for (int i = 0; i < numOfFeeds; i++) { // set schedules time and food portion
                    scheduleTime[i].setText(String.format("%02d : %02d", hour.get(i), minute.get(i)));   // set time that add to schedule
                    schedulePortion[i].setText(setPortion(food.get(i)));                   // set portion that add to schedule
                }
                numOfFeeds--; // decrease value  by 1
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void removeFromSchedule() { // function for removing on time and portion from schedules
        AlertDialog.Builder alertDialog = new AlertDialog.
                Builder(FeedingTime.this);       // create an alert dialog (pop up window) in this activity
        alertDialog.setTitle("Select schedule");        // set title of alert dialog
        final int[] checkedItem = {-1};                // create an unchangeable array of
        // integers that indicate which item the user select
        final String[] items = {"1", "2", "3", "4"};// create an unchangeable array of strings
        alertDialog.setSingleChoiceItems(items, checkedItem[0], (dialog, which) -> { // set a list of items to be displayed in the dialog as the content
            checkedItem[0] = which;                                                  // save the select item in checkedItem variable
            Log.d(TAG, String.valueOf(checkedItem[0]));                              //
        });

        Log.d(TAG, String.valueOf(numOfFeeds));
        alertDialog.setPositiveButton("Confirm", (dialog, which) -> { // set confirm button
            if (checkedItem[0] != -1 && checkedItem[0] <= numOfFeeds) { // if the user selected item to remove that exist
                for (int i = checkedItem[0]; i < numOfFeeds; i++) { // rearrange items in table

                    FeedSchedule feedSchedule = new FeedSchedule(food.get(i + 1),
                            i, hour.get(i + 1), minute.get(i + 1)); // create new FeedSchedule class to hold portion and feeding time
                    schedule_ref.child(String.valueOf(currentPet)).
                            child(String.valueOf(i)).setValue(feedSchedule); //update FeedSchedule class to firebase
                }
                schedule_ref.child(String.valueOf(currentPet)).
                        child(String.valueOf(numOfFeeds)).removeValue(); // remove last available from firebase
                scheduleTime[numOfFeeds].setText("-- : --"); // set removed time to "-- : --"
                schedulePortion[numOfFeeds].setText("N/A");  // set removed portion to "N/A"
                schedule_ref.child("num_of_feed").setValue(--numOfFeeds);   // decrease feed schedule counter
            }
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) ->
                Log.d(TAG, "simpleAlert: canceled"));// set a listener to be invoked
        // when the positive button of the dialog is pressed

        AlertDialog alert = alertDialog.create();// creates an AlertDialog with the arguments supplied to this builder.
        alert.setCanceledOnTouchOutside(false);  // sets whether this dialog is canceled when touched outside the window's bounds.
        // if setting to true, the dialog is set to be cancelable if not already set.
        alert.show();                            // start the dialog and display it on screen.
    }

    private String setPortion(Integer option) {  // function that convert option to true value
        String[] values =                                        // create a list of strings variable that represent the portion of food
                {"200 grams", "250 grams",                       //
                        "300 grams", "450 grams"};  //
        return values[option - 1]; // return portion size
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}
