package com.example.autofeed.fragments;

import static java.lang.String.format;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.autofeed.R;
import com.example.autofeed.activities.FeedingTime;
import com.example.autofeed.classes.FeedSchedule;
import com.example.autofeed.classes.FeedTime;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Functions extends Fragment {

    private static final String TAG = "TAG";
    private Spinner spinner1, spinner2, spinner3;                // define spinner variable
    private TextView setTime;                                    // define text variable
    private Button openSchedule, addSchedule, btnFeedNow;        // define variable
    private List<String> addFeed = new ArrayList<>();           // define dynamic list of strings variable
    private FirebaseAuth auth;                                   // define FireBase authentication variable
    private DatabaseReference reference, reference1, feed_time_ref; // define access realtime database variable
    private int pet, food, hour, minute, numOfFeeds, isFeed;     // define int variables

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_functions, container, false);

        auth = FirebaseAuth.getInstance();                                                           // get access for FireBase authentication
        reference = FirebaseDatabase.getInstance().getReference("Users").child(encodeUserEmail(// access branch in realtime database :
                Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()) // "Users/(user's email)/Feed Time"
        )).child(("Feed Time"));                                                         //
//        reference1 = FirebaseDatabase.getInstance().getReference("Door_rotation"); // access branch in realtime database : "Door_rotation"
        feed_time_ref = reference.child("schedule");     // access branch in realtime database : "(reference)/Feed_Time"


        spinner1 = view.findViewById(R.id.spinner1);            //
        spinner2 = view.findViewById(R.id.spinner2);            //
        spinner3 = view.findViewById(R.id.spinner3);            //
        setTime = view.findViewById(R.id.tvSetTime);            // link UI to code
        btnFeedNow = view.findViewById(R.id.button);            //
        openSchedule = view.findViewById(R.id.btnOpenSchedule); //
        addSchedule = view.findViewById(R.id.btnAddSchedule);   //

        readFireBase(); // execute function that read data from FireBase
        initspinnerfooter(); // execute function for spinner setup


        btnFeedNow.setOnClickListener(v -> feedNow(view));    // if btnFeedNow button was pressed execute feedNow() function
        openSchedule.setOnClickListener(v -> showSchedule()); // if openSchedule button was pressed execute showSchedule() function
        setTime.setOnClickListener(v -> setFeedTime());       // if setTime button was pressed execute setFeedTime() function
        addSchedule.setOnClickListener(v -> addToSchedule()); // if addSchedule button was pressed execute setFeedTime() function
        return view;
    }

    private void addToSchedule() { // function for adding portion of food and deeding time to firebase
        if (numOfFeeds == 4)
            Toast.makeText(getActivity(), "Max settings", Toast.LENGTH_SHORT).show();
        else {
            if (food != 0 && pet != 0) {
                FeedSchedule feedSchedule = new FeedSchedule(food,
                        numOfFeeds, hour - 1, minute); // create new FeedSceduale class to hold portion and feeding time
                feed_time_ref.child(String.valueOf(pet)).child(String.valueOf(numOfFeeds)).setValue(feedSchedule);// upload class to firebase
                feed_time_ref.child("num_of_feed").setValue(++numOfFeeds);
                Toast.makeText(getActivity(), "Added to schedule", Toast.LENGTH_SHORT).show();   // notify the user via pop up message
            } else
                Toast.makeText(getActivity(), "Enter all details", Toast.LENGTH_SHORT).show();  // notify the user via pop up message
        }
    }

    private void showSchedule() {                                   // function that open feeding schedule
        startActivity(new Intent(getActivity(), FeedingTime.class));// start FeedingTime activity
    }

    private void setFeedTime() {              // function that set feed time
        Calendar cal = Calendar.getInstance();// create a Calendar variable and get a calendar using the default time zone and locale.
        hour = cal.get(Calendar.HOUR_OF_DAY); // set the hour of the day
        minute = cal.get(Calendar.MINUTE);    // set minute

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),                         // create a TimePickerDialog variable to let the user pick time
                (view1, hour1, minute1) ->                                                              //
                        saveDateFireBase(new FeedTime(String.valueOf(hour1), String.valueOf(minute1))), // execute a function that save time in firebase
                hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));            // set the time format to 24 hours
        timePickerDialog.show();                                                                        // show dialog on screen
    }

    private void initspinnerfooter() {                           // function that create initialize the spinners
        String[] values =                                        // create a list of strings variable that represent the portion of food
                {"select", "200 grams", "250 grams",             //
                        "300 grams", "450 grams"};  //
        String[] time =                                          //create a list of strings variable that represent the portion of food
                {"select", "1", "2", "3"};                       //

        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_dropdown_item_1line, values); // create a spinner adapter
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_dropdown_item_1line, time);  // create a spinner adapter

        spinner1.setAdapter(adapter);  // set spinner adapter
        spinner2.setAdapter(adapter1); // set spinner adapter
        spinner3.setAdapter(adapter);  // set spinner adapter

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {     // set listener on item selected
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {  //
                food = position;                                                                   // set position of item
                Log.v("item", (String) parent.getItemAtPosition(position));                   //
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);                // set color of spinner text
                reference.child("Portion").setValue(String.valueOf(position));      // upload portion to firebase at "(reference)/Portion"
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {         // set listener on item selected
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//
                pet = position;                                                                    //set position of item
                Log.v("item", (String) parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);               //set color of spinner text
                reference.child("pet").setValue(String.valueOf(position));                       //upload portion to firebase at "(reference)/pet"
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {             //set listener on item selected
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { // set position of item
                Log.v("item", (String) parent.getItemAtPosition(position));                  //
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                reference.child("Now").child("portion").setValue(position);//upload portion to firebase at "(reference)/pet"
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void feedNow(View view) {
        isFeed = 1;
        Snackbar.make(view.findViewById(R.id.functions), "Feeding Now", Snackbar.LENGTH_SHORT).show();
        Log.d(TAG, "Feeding Now");
        reference.child("Now").child("isFeed").setValue(isFeed);//upload portion to firebase at "(reference)/pet"
//        reference1.setValue(isFeed);

    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    private void saveDateFireBase(FeedTime feedTime) { // function that upload feed time to firebase
        reference.child("time").setValue(feedTime);    // upload time to "(reference) / time "

    }

    private void readFireBase() { // function that read data from firebase
        reference.addValueEventListener(new ValueEventListener() { // listen to the branch reference is set and retrieve data from it
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FeedTime feedTime1 = dataSnapshot.child("time").getValue(FeedTime.class); // retrieve data about feeding time
                // from "(reference) / time"  branch to new FeedTime class variable
                FeedTime feedTime1Temp = new FeedTime(); // create default FeedTime class
                //  FeedTime feedTime2Temp = new FeedTime();
                String portion = (String) dataSnapshot.child("Portion").getValue(); // create string variable that hold
                // the amount of food from : "(reference)/ Portion"

                if (feedTime1 != null) { // if feedTime1 is not empty
                    setTime.setText(format("%02d : %02d",
                            Integer.parseInt(feedTime1.getHour()), Integer.parseInt(feedTime1.getMinute()))); // display time
                    hour = Integer.parseInt(feedTime1.getHour());
                    minute = Integer.parseInt(feedTime1.getMinute());
                } else { // otherwise
                    setTime.setText(format("%02d : %02d",
                            Integer.parseInt(feedTime1Temp.getHour()), Integer.parseInt(feedTime1Temp.getMinute())));//display default time
                }
                if (portion != null)                                  // if portion is not null
                    spinner1.setSelection(Integer.parseInt(portion)); // set spinner selection at portion
                else                                                  // otherwise
                    spinner1.setSelection(0);                         // set spinner selection at index 0

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        feed_time_ref.child("num_of_feed").addValueEventListener(new ValueEventListener() {                             // listen to the branch feed_time_ref is set and retrieve data from it
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    numOfFeeds = dataSnapshot.getValue(Integer.class);
                    Log.d(TAG, "if");
                } else {
                    feed_time_ref.child("num_of_feed").setValue(0);
                    Log.d(TAG, "else");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
