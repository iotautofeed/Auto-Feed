package com.example.autofeed.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.autofeed.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedingTime extends AppCompatActivity {
    private TextView feedTime1, feedTime2, feedTime3, feedTime4; //define text variables
    private TextView portion1, portion2, portion3, portion4;     //define text variables
    private SwitchCompat sw1, sw2, sw3, sw4;                     //define switch variables
    private DatabaseReference reference;                         //define access realtime database variable
    private TextView[] scheduleTime, schedulePortion;            //define an array of textviews variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding_time);

        reference = FirebaseDatabase.getInstance().getReference("switch"); //  access branch in realtime database : "switch"
        setVariables();                                                          // execute function for linking the UI to the code

        scheduleTime = new TextView[]{feedTime1, feedTime2, feedTime3, feedTime4}; // initialize the array
        schedulePortion = new TextView[]{portion1, portion2, portion3, portion4};  // initialize the array

        sw1.setOnClickListener(v -> setSwitch1()); // if sw1 was pressed execute setSwitch1() function
        sw2.setOnClickListener(v -> setSwitch2()); // if sw2 was pressed execute setSwitch2() function
        sw3.setOnClickListener(v -> setSwitch3()); // if sw3 was pressed execute setSwitch3() function
        sw4.setOnClickListener(v -> setSwitch4()); // if sw4 was pressed execute setSwitch4() function
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


    }

    private void setSwitch1() {                 // function for handle switch state and update firebase

        if (sw1.isChecked()) {                  // if switch checked
            reference.child("sw1").setValue(1); // set value in branch : "(reference) / sw1"
        } else {                                // otherwise
            reference.child("sw1").setValue(0); // set value in branch : "(reference) / sw1"
        }
    }

    private void setSwitch2() {                 // function for handle switch state and update firebase
        if (sw1.isChecked()) {                  // if switch checked
            reference.child("sw2").setValue(1); // set value in branch : "(reference) / sw2"
        } else {                                // otherwise
            reference.child("sw2").setValue(0); // set value in branch : "(reference) / sw2"
        }
    }

    private void setSwitch3() {                 // function for handle switch state and update firebase
        if (sw1.isChecked()) {                  // if switch checked
            reference.child("sw3").setValue(1); // set value in branch : "(reference) / sw3"
        } else {                                // otherwise
            reference.child("sw3").setValue(0); // set value in branch : "(reference) / sw3"
        }
    }

    private void setSwitch4() {                 // function for handle switch state and update firebase
        if (sw1.isChecked()) {                  // if switch checked
            reference.child("sw4").setValue(1); // set value in branch : "(reference) / sw4"
        } else {                                // otherwise
            reference.child("sw4").setValue(0); // set value in branch : "(reference) / sw4"
        }
    }

    private void getDataFireBase() {

    }

}