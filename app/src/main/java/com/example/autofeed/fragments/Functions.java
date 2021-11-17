package com.example.autofeed.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.autofeed.R;
import com.example.autofeed.classes.FeedTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;


public class Functions extends Fragment {

    private Spinner spinner1, spinner2, spinner3;
    private SwitchCompat swOn_Off;
    private TextView setStartTime, setEndTime;
    private View v;
    private Context context;

    private FirebaseAuth auth;          //authentication
    private FirebaseDatabase rootNode;  //real time database
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_functions, container, false);


        auth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Users").child(encodeUserEmail(Objects.requireNonNull
                (Objects.requireNonNull(auth.getCurrentUser()).getEmail())
        )).child(("Feed Time"));


        spinner1 = view.findViewById(R.id.spinner1);
        spinner2 = view.findViewById(R.id.spinner2);
        spinner3 = view.findViewById(R.id.spinner3);
        setStartTime = view.findViewById(R.id.tvSetTime);
        setEndTime = view.findViewById(R.id.tvSetTime1);
        swOn_Off = view.findViewById(R.id.switchButton);

        spinner1.setEnabled(false);
        spinner2.setEnabled(false);

        swOn_Off.setOnClickListener(v -> setSwitch());

        initspinnerfooter();

        return view;
    }

    private void setFeedTime(TextView view) {
        Calendar cal = Calendar.getInstance();
        final int hour = cal.get(Calendar.HOUR_OF_DAY);
        final int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (view1, hour1, minute1) -> {
                    if (view == setStartTime) {
                        saveDateFireBase(new FeedTime(hour1, minute1, 0));
                    } else
                        saveDateFireBase(new FeedTime(hour1, minute1, 1));
                    view.setText(String.format("%02d : %02d", hour1, minute1));
                },
                hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();
    }

    private void setSwitch() {

        if (swOn_Off.isChecked()) {
            reference.child("TIMER").setValue("true");
            spinner1.setEnabled(true);
            spinner2.setEnabled(true);
            setStartTime.setEnabled(true);
            setEndTime.setEnabled(true);
            setStartTime.setOnClickListener(v -> setFeedTime(setStartTime));
            setEndTime.setOnClickListener(v -> setFeedTime(setEndTime));
        } else {
            reference.child("TIMER").setValue("false");
            spinner1.setEnabled(false);
            spinner2.setEnabled(false);
            setStartTime.setEnabled(false);
            setEndTime.setEnabled(false);

        }
    }

    private void initspinnerfooter() {
        String[] values =
                {"select","100 grams", "150 grams", "200 grams", "250 grams", "300 grams", "350 grams"};
        String[] time =
                {"select", "1 hour", "2 hours", "3 hours", "3 hours", "4 hours", "5 hours"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, values);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, time);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter1);
        spinner3.setAdapter(adapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                if (position == 0)
                    reference.child("Every").setValue("None");
                else
                    reference.child("Portion").setValue(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                if (position == 0)
                    reference.child("Every").setValue("None");
                else
                    reference.child("Every").setValue(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    private void saveDateFireBase(FeedTime feedTime) {
        if ((feedTime.getState() == 0)) {
            reference.child("Start").setValue(feedTime);
        } else
            reference.child("End").setValue(feedTime);
    }

    
}
