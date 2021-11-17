package com.example.autofeed.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.autofeed.R;
import com.example.autofeed.classes.PetInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends Fragment {


    private static final String TAG = "TAG";
    private FirebaseAuth auth;          //authentication
    private FirebaseDatabase rootNode;  //real time database
    private DatabaseReference reference;

    private CircleImageView petImage;
    private TextView pet_Name;
    private BarChart barChart;
    private BarDataSet barDataSet;
    private BarData barData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        pet_Name = view.findViewById(R.id.tvPetName);
        barChart = view.findViewById(R.id.chart);
        petImage=view.findViewById(R.id.civPetImg);


        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").
                child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser())
                        .getEmail()))).child("Pet Info");

        readFireBase();
        setPetImage();

        ArrayList<BarEntry> food = new ArrayList<>();
        food.add(new BarEntry(1, 100));
        food.add(new BarEntry(2, 200));
        food.add(new BarEntry(3, 300));
        food.add(new BarEntry(4, 400));
        food.add(new BarEntry(5, 300));
        food.add(new BarEntry(6, 250));

        barDataSet = new BarDataSet(food, "Food");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(14f);

        barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Bar Chart");
        barChart.animateY(1000);

        return view;

    }

    private void readFireBase() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PetInfo petInfo = dataSnapshot.getValue(PetInfo.class);

                if (petInfo != null) {
                    pet_Name.setText(petInfo.getName());

                } else {
                    PetInfo petInfoTemp = new PetInfo();
                    pet_Name.setText(petInfoTemp.getName());
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

    private void setPetImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(petImage);
        }
    }
}
