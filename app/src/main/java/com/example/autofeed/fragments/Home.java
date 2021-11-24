package com.example.autofeed.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends Fragment {


    private static final String TAG = "TAG";
    private final int max = 100;
    private FirebaseAuth auth;          //authentication
    private FirebaseDatabase rootNode;  //real time database
    private DatabaseReference reference;

    private CircleImageView petImage;
    private TextView pet_Name, nextFeedTime, bowlStatus, containerStatus;
    private BarChart barChart;
    private BarDataSet barDataSet;
    private BarData barData;
    private ProgressBar progressBar, progressBar1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        pet_Name = view.findViewById(R.id.tvPetName);
        nextFeedTime = view.findViewById(R.id.tvShowNextTime);
        barChart = view.findViewById(R.id.chart);
        petImage = view.findViewById(R.id.civPetImg);
        progressBar = view.findViewById(R.id.pbProgressBar);
        progressBar1 = view.findViewById(R.id.pbProgressBar1);
        bowlStatus = view.findViewById(R.id.tvBowlStatus);
        containerStatus = view.findViewById(R.id.tvContainerStatus);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").
                child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser())
                        .getEmail())));
        readFireBase();
        setPetImage();
        setGraph();
//
//        ArrayList<BarEntry> food = new ArrayList<>();
//        food.add(new BarEntry(1, 100));
//        food.add(new BarEntry(2, 200));
//        food.add(new BarEntry(3, 300));
//        food.add(new BarEntry(4, 400));
//        food.add(new BarEntry(5, 300));
//        food.add(new BarEntry(6, 250));

//        barDataSet = new BarDataSet(food, "Food");
//        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(14f);
//
//        barData = new BarData(barDataSet);
//
//        barChart.setFitBars(true);
//        barChart.setData(barData);
//        barChart.getDescription().setText("Bar Chart");
//        barChart.animateY(1000);

        return view;

    }

    private void readFireBase() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PetInfo petInfo = dataSnapshot.child("Pet Info").getValue(PetInfo.class);
                String swTimer = (String) dataSnapshot.child(("Feed Time")).child("Timer").getValue();
                String bowl = (String) dataSnapshot.child("Food Status").child("bowl").getValue();
                String container = (String) dataSnapshot.child("Food Status").child("container").getValue();

                if (petInfo != null) {
                    pet_Name.setText(petInfo.getName());

                } else {
                    PetInfo petInfoTemp = new PetInfo();
                    pet_Name.setText(petInfoTemp.getName());
                }
                if (swTimer != null) {
                    if (swTimer.equals("true")) {
                        Calendar cal = Calendar.getInstance();
                        nextFeedTime.setText(new SimpleDateFormat("hh : mm").format(cal.getTime()));
                    } else
                        nextFeedTime.setText("-- : --");
                }
                if (bowl != null) {
                    bowlStatus.setText(bowl + "%");
                    progressBar.setProgress(Integer.parseInt(bowl));
                    setProgressBarColor(Integer.parseInt(bowl), progressBar);
                } else {
                    bowlStatus.setText("0%");
                    progressBar.setProgress(0);
                }
                if (container != null) {
                    containerStatus.setText(container + "%");
                    progressBar1.setProgress(Integer.parseInt(container));
                    setProgressBarColor(Integer.parseInt(container), progressBar1);

                } else {
                    containerStatus.setText("0%");
                    progressBar1.setProgress(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setProgressBarColor(int percentage, ProgressBar progress) {
        LayerDrawable progressBarDrawable = (LayerDrawable) progress.getProgressDrawable();
        Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
        Drawable progressDrawable = progressBarDrawable.getDrawable(1);
        backgroundDrawable.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.white), PorterDuff.Mode.SRC_IN);

        if (percentage > 75) {
            progressDrawable.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.green), PorterDuff.Mode.SRC_IN);
        } else if (percentage > 50) {
            progressDrawable.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.lightGreen), PorterDuff.Mode.SRC_IN);
        } else if (percentage > 25) {
            progressDrawable.setColorFilter(ContextCompat.getColor(this.getContext(), android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_IN);
        } else {
            progressDrawable.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.red), PorterDuff.Mode.SRC_IN);
        }
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

    private void setGraph() {
        progressBar1.setMax(100);
        progressBar.setProgress(0);
        progressBar.setMax(max);
        progressBar.setProgress(0);
        final int[] day = {0};
        List<String> list = new ArrayList<>();
        DatabaseReference graphReference = FirebaseDatabase.getInstance().getReference().child("FoodPerDay");

        graphReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String food = ds.getValue(String.class);
                        if (food != null) {
                            list.add(food);
                            Log.d("Got value" + food, TAG);

                        }
                    }
                }
                ArrayList<BarEntry> data = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    data.add(new BarEntry(i, Integer.parseInt((list.get(i)))));
                    Log.d("Got value " + list.get(i), TAG);

                }
                barDataSet = new BarDataSet(data, "Food");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(14f);

                barData = new BarData(barDataSet);

                barChart.setFitBars(true);
                barChart.setData(barData);
                barChart.getDescription().setText("Bar Chart");
                barChart.animateY(1000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed", TAG);

            }
        });
    }
}