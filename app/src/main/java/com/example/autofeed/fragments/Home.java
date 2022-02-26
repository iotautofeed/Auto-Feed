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
import com.example.autofeed.activities.PetProfile;
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
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends Fragment {


    private static final String TAG = "TAG";
    private final int max = 100;                     // define a final int variable
    private FirebaseAuth auth;                       // define FireBase authentication variable
    private DatabaseReference reference, reference1; // define access realtime database variable

    private List<String> pets = new ArrayList<>(); //define dynamic list variable

    private CircleImageView petImage;                                    // define circle image variable
    private TextView petName, nextFeedTime, bowlStatus, containerStatus; // define text variable
    private BarChart barChart;                                           //define bar chart variable
    private BarDataSet barDataSet;                                       //define variable
    private BarData barData;                                             //define bar data variable
    private ProgressBar progressBar, progressBar1;                       //define progress bar variable
    private String currentPet;                                           //define string variable


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);// Inflate the layout for this fragment


        petName = view.findViewById(R.id.tvPetName);                //
        nextFeedTime = view.findViewById(R.id.tvShowNextTime);      //
        barChart = view.findViewById(R.id.chart);                   //
        petImage = view.findViewById(R.id.civPetImg);               // linking the UI to the code
        progressBar = view.findViewById(R.id.pbProgressBar);        //
        progressBar1 = view.findViewById(R.id.pbProgressBar1);      //
        bowlStatus = view.findViewById(R.id.tvBowlStatus);          //
        containerStatus = view.findViewById(R.id.tvContainerStatus);//

        auth = FirebaseAuth.getInstance();                                                                               // get access for FireBase authentication
        reference = FirebaseDatabase.getInstance().getReference().child("Users").                                        // access branch : "Users/ (current user email) in realtime database
                child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail())));//

        reference1 = FirebaseDatabase.getInstance().getReference().child("Pets");/// access branch : "Pets"  in realtime database


        progressBar.setMax(max);   //set max range of progressbar to 100
        progressBar.setProgress(0);//set initial value 0
        progressBar1.setMax(max);  //set max range of progressbar1 to 100
        progressBar.setProgress(0);//set initial value 0

        readFireBase();   //execute function that read data from FireBase
        readFireBasePet();//execute function that read data from FireBase
        setGraph();       //execute function that read data from FireBase
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

    private void readFireBasePet() { // function that check if the user have pets or not, set the name and image of current pet

        reference1.addValueEventListener(new ValueEventListener() { // listen to the branch reference1 is set and retrieve data from it
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pets.clear();// clear the list's data
                for (DataSnapshot ds : dataSnapshot.child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()))).getChildren()) {// get all pets id of the current user
                    if (ds.exists()) {                                                         //if there pets add their id to pets variable
                        pets.add(Objects.requireNonNull(ds.child("id").getValue()).toString());//
                    }
                }
                Log.d(TAG, String.valueOf(pets));

                currentPet = dataSnapshot.child("Current Pet").getValue(String.class); // get current pet id
                if (pets.size() > 0) { //if there pets
                    Log.d(TAG, currentPet);
                    if (currentPet != null) { // if the user have a pet
                        PetInfo petInfo = dataSnapshot.child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()))) // create a new class that store the data
                                .child(pets.get(Integer.parseInt(currentPet))).getValue(PetInfo.class);                                                         // of the current pet
                        if (petInfo != null) {                     // if petInfo is not empty
                            if (isAdded()) {                       //
                                petName.setText(petInfo.getName());// set pet name on screen
                                Glide.with(getContext())           //
                                        .load(petInfo.getImageID())// load pet image
                                        .into(petImage);           //
                            }
                        } else {                                   // if petInfo is empty (the user don't have a pet)
                            PetInfo petInfoTemp = new PetInfo();   // create an empty class of PetInfo
                            petName.setText(petInfoTemp.getName());//set pet name on screen
                        }
                    }
                } else {                                          // if the current user doesn't have pets
                    reference1.child("Current Pet").setValue("0");// set "Current Pet" to 0
                    {
                        PetInfo petInfoTemp = new PetInfo();   // create an empty class of PetInfo
                        petName.setText(petInfoTemp.getName());//set pet name on screen
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void readFireBase() { // function that read data from firebase
        reference.addValueEventListener(new ValueEventListener() { // listen to the branch reference is set and retrieve data from it
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PetInfo petInfo = dataSnapshot.child("Pet Info").getValue(PetInfo.class);                   // create variable
                String swTimer = (String) dataSnapshot.child(("Feed Time")).child("Timer").getValue();      // create variable
                String bowl = (String) dataSnapshot.child("Food Status").child("bowl").getValue();          // create variable that hold the amount of food in the bowl
                String container = (String) dataSnapshot.child("Food Status").child("container").getValue();// create variable that hold the amount of food in the container

                if (petInfo != null) {
                    petName.setText(petInfo.getName());

                } else {
                    PetInfo petInfoTemp = new PetInfo();
                    petName.setText(petInfoTemp.getName());
                }
                if (swTimer != null) {                                                                     // if the timer is not null
                    if (swTimer.equals("true")) {                                                          // if the timer is not null
                        Calendar cal = Calendar.getInstance();                                             // create a variable that hold the current time
                        nextFeedTime.setText(new SimpleDateFormat("hh : mm").format(cal.getTime()));//
                    } else
                        nextFeedTime.setText("-- : --");                                                   //
                }
                if (bowl != null) {                                          // if bowl is not empty
                    bowlStatus.setText(bowl + "%");                          // set percentage of current food in the bowl as text
                    progressBar.setProgress(Integer.parseInt(bowl));         // set percentage of current food in the bowl as progress bar
                    setProgressBarColor(Integer.parseInt(bowl), progressBar);// execute a function that color progress bar
                } else {                       // if bowl is  empty
                    bowlStatus.setText("0%");  // set percentage of current food in the bowl as text
                    progressBar.setProgress(0);// set percentage of current food in the bowl as progress bar
                }
                if (container != null) {                                           // if container is not empty
                    containerStatus.setText(container + "%");                      // set percentage of current food in the bowl as text
                    progressBar1.setProgress(Integer.parseInt(container));         // set percentage of current food in the bowl as progress bar
                    setProgressBarColor(Integer.parseInt(container), progressBar1);// execute a function that color progress bar

                } else {                          // if container is empty
                    containerStatus.setText("0%");// set percentage of current food in the bowl as text
                    progressBar1.setProgress(0);  // set percentage of current food in the bowl as progress bar
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setProgressBarColor(int percentage, ProgressBar progress) {               // function that color the progress bar with respect to the percentage
        LayerDrawable progressBarDrawable = (LayerDrawable) progress.getProgressDrawable();// create a variable that hold an array of drawables.
                                                                                           // These are drawn in array order, so the element with the largest index will be drawn on top.
        Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);            // set background color at index 0 of progressBarDrawable
        Drawable progressDrawable = progressBarDrawable.getDrawable(1);              // set progress color at index 0 of progressBarDrawable
        backgroundDrawable.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.white), PorterDuff.Mode.SRC_IN); // set background color to white

        // set color of progress bar with respect to percentage
        if (percentage > 75) {
            progressDrawable.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.green), PorterDuff.Mode.SRC_IN);                   //set progress color to green
        } else if (percentage > 50) {
            progressDrawable.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.lightGreen), PorterDuff.Mode.SRC_IN);              //set progress color to light green
        } else if (percentage > 25) {
            progressDrawable.setColorFilter(ContextCompat.getColor(this.getContext(), android.R.color.holo_orange_dark), PorterDuff.Mode.SRC_IN);//set progress color to orange
        } else {
            progressDrawable.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.red), PorterDuff.Mode.SRC_IN);                     //set progress color to red
        }
    }

    private void setGraph() {      //function that plot the amount of food the pet ate per day


        List<String> list = new ArrayList<>(); // create a dynamic list of strings variable
        DatabaseReference graphReference = FirebaseDatabase.getInstance().getReference().child("FoodPerDay"); // access branch in realtime database "FoodPerDay"

        graphReference.addValueEventListener(new ValueEventListener() { // /listen to the branch graphReference is set and retrieve data from it
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {                             // if the branch exist
                    for (DataSnapshot ds : dataSnapshot.getChildren()) { //
                        String food = ds.getValue(String.class);         // get the data from the branch
                        if (food != null) {                              // if the data is mot null
                            list.add(food);                              // add to list variable
                            Log.d("Got value" + food, TAG);

                        }
                    }
                }
                ArrayList<BarEntry> data = new ArrayList<>(); // create a dynamic list of BarEntry(x and y coordinates) variable

                for (int i = 0; i < list.size(); i++) {                        // add the amount of food the pet ate to
                    data.add(new BarEntry(i, Integer.parseInt((list.get(i)))));// data variable
                    Log.d("Got value " + list.get(i), TAG);

                }
                barDataSet = new BarDataSet(data, "Food");     // create a variable that hold the data and labal of the graph
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);// set colors of the graph
                barDataSet.setValueTextColor(Color.BLACK);          // set the color the value-labels
                barDataSet.setValueTextSize(14f);                   // set the text-size of the value-labels

                barData = new BarData(barDataSet);

                barChart.setFitBars(true); // set the fit shape of the bars
                barChart.setData(barData); //sets a new data object for the chart. The data object contains all values and information needed for displaying.
                barChart.getDescription().setText("Bar Chart");//
                barChart.animateY(1000); // set animation on the y - axis that last 1 second
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed", TAG);

            }
        });
    }
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

}