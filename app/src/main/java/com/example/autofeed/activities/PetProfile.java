package com.example.autofeed.activities;

import static android.widget.Toast.makeText;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.autofeed.R;
import com.example.autofeed.classes.PetInfo;
import com.github.sealstudios.fab.FloatingActionButton;
import com.github.sealstudios.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class PetProfile extends AppCompatActivity {

    private static final String TAG = "TAG";
    private static final int RESULT_LOAD_IMAGE = 1;                                     // define int variable
    private CircleImageView petImage;                                                   // define CircleImageView variable for pet image
    private TextView editButton, editName, editType, editBreed, editGender, editWeight; // define text variable
    private List<String> pets = new ArrayList<>();                                      // define dynamic list of strings variable
    private List<String> petsName = new ArrayList<>();                                  // define dynamic list of strings variable
    private String currentPet;                                                          // define string variable
    private FloatingActionButton editPet, changePet, addPet;                            // define FloatingActionButton variable for buttons
    private FloatingActionMenu fabMenu;                                                 // define FloatingActionMenu variable for button

    private FirebaseAuth auth;          // define variable for FireBase authentication
    private DatabaseReference reference;// define variable for access realtime database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pet_profile);

        setVariables();  // function for variables setup

        auth = FirebaseAuth.getInstance();                                      // get access for FireBase authentication
        reference = FirebaseDatabase.getInstance().getReference().child("Pets");// access branch in realtime database "Users"

        readFireBase();//execute function that read data from FireBase

        addPet.setOnClickListener(view -> editDetails());       // if addPet button was pressed execute editDetails() function
        editPet.setOnClickListener(view -> editDetails());      // if editPet button was pressed execute editDetails() function
        changePet.setOnClickListener(view -> showAlertDialog());// if changePet button was pressed execute showAlertDialog() function

    }

    private void setVariables() {                           //function for linking the UI to the code
        petImage = findViewById(R.id.civPetImg);            //
        editButton = findViewById(R.id.tvEdit);             //
        editName = findViewById(R.id.tvPetName);            //
        editType = findViewById(R.id.tvEdit_Pet_Type);      //
        editBreed = findViewById(R.id.tvEdit_Pet_Breed);    //
        editGender = findViewById(R.id.tvEdit_Pet_Gender);  //
        editWeight = findViewById(R.id.tvEdit_Pet_Weight);  //
        addPet = findViewById(R.id.fabAddPet);              //
        editPet = findViewById(R.id.fabEditPet);            //
        changePet = findViewById(R.id.fabChangePet);        //
        fabMenu = findViewById(R.id.fabMenu);               //

    }

    //Select Pet & Set Details
    private void showAlertDialog() {                                                        // function for select Pet showed on screen
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PetProfile.this); // create an alert dialog (pop up window) in this activity
        alertDialog.setTitle("Select Pet");                                                 // set title of alert dialog
        String[] items = petsName.toArray(new String[0]);                                   // create an array of strings variable and pass the names of the pets to it
        final int[] checkedItem = {-1};                                                     // create an unchangeable array of integers that indicate which item the user select
        alertDialog.setSingleChoiceItems(items, checkedItem[0], (dialog, which) -> {        // set a list of items to be displayed in the dialog as the content
            checkedItem[0] = which;                                                         // save the select item in checkedItem variable
            Log.d(TAG, String.valueOf(checkedItem[0]));                                     //
        });

        alertDialog.setPositiveButton("Confirm", (dialog, which) -> {                  //
            if (checkedItem[0] != -1)                                                       //
                setPetProfile(checkedItem[0]);                                              //
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> Log.d(TAG, "simpleAlert: canceled"));// set a listener to be invoked when the positive button of the dialog is pressed

        AlertDialog alert = alertDialog.create();// creates an AlertDialog with the arguments supplied to this builder.
        alert.setCanceledOnTouchOutside(false);  // sets whether this dialog is canceled when touched outside the window's bounds. if setting to true, the dialog is set to be cancelable if not already set.
        alert.show();                            // start the dialog and display it on screen.
    }

    private void setPetProfile(int changePet) {                                 // function for setup pet's profile
        reference.child("Current Pet").setValue(String.valueOf(changePet));     // set the data at this location to the given value
        readFireBase();                                                         // execute function that read data from FireBase

    }

    private void setImage() {                                                  // function for set up pet image
        Intent intent = new Intent(Intent.ACTION_PICK,                         //
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//
        startActivityForResult(intent, RESULT_LOAD_IMAGE);                     //
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //
        super.onActivityResult(requestCode, resultCode, data);                      //
        if (resultCode == RESULT_OK && null != data) {                              //
            Uri selectedImage = data.getData();                                     //
            petImage.setImageURI(selectedImage);                                    //
        }
    }

    private void editDetails() {                                                        //function that start Edit Pet Activity
        petImage.setOnClickListener(view -> setImage());                                //
        Intent intent = new Intent(PetProfile.this, EditPetProfile.class); // create an Intent variable to pass data btween PetProfile activity to EditPetProfile activity
        if (pets.isEmpty() || addPet.isPressed()) {                                     // if the user does not have pots or addPet button is pressed
            intent.putExtra("new", "true");                                 // put the value "true" in the name "new" and extend the  data in Intent
            intent.putExtra("id", String.valueOf(pets.size()));                   // put the value of pets array size in the name "id" and extend the  data in Intent
        } else {                                                                        // otherwise
            intent.putExtra("new", "false");                                // put the value "false" in the name "new" and extend the  data in Intent
            intent.putExtra("id", pets.get(Integer.parseInt(currentPet)));        // put the value current pet the name "id" and extend the  data in Intent
        }

        startActivity(intent); // start EditPetProfile activity and pass the intent
    }

    private void readFireBase() {   // function that read data from firebase

        reference.addValueEventListener(new ValueEventListener() { // listen to the branch reference is set and retrieve data from it
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pets.clear();    // clear list's data
                petsName.clear();// clear list's data
                for (DataSnapshot ds : dataSnapshot.child(encodeUserEmail(Objects.requireNonNull
                        (Objects.requireNonNull(auth.getCurrentUser()).getEmail()))).getChildren()) { // get all pets id of the current user
                    if (ds.exists()) {                                                                // check if the user have pets
                        pets.add(Objects.requireNonNull(ds.child("id").getValue()).toString());       // add pet id to pets variable
                        petsName.add(Objects.requireNonNull(ds.child("name").getValue()).toString()); // add pet name to petName variable
                    }
                }
                Log.d(TAG, String.valueOf(pets));
                Log.d(TAG, String.valueOf(petsName));

                currentPet = dataSnapshot.child("Current Pet").getValue(String.class); // get current pet id
                if (pets.size() > 0) { //if there pets
                    Log.d(TAG, currentPet);
                    if (currentPet != null) { // if the user have a pet
                        PetInfo petInfo = dataSnapshot
                                .child(encodeUserEmail(Objects.requireNonNull    //
                                (Objects.requireNonNull(auth.getCurrentUser())   //  create a new class that store the credentials
                                .getEmail())))                                   //  of the current pet
                                .child(pets.get(Integer.parseInt(currentPet)))   //
                                .getValue(PetInfo.class);                        //

                        if (petInfo != null) {                               // if petInfo is not empty
                            editName.setText(petInfo.getName());             // set pet's name on screen
                            editType.setText(petInfo.getType());             // set pet's type on screen
                            editBreed.setText(petInfo.getBreed());           // set pet's breed on screen
                            editGender.setText(petInfo.getGender());         // set pet's gender on screen
                            editWeight.setText(petInfo.getWeight() + " kgs");// set pet's weight on screen
                            Glide.with(getApplicationContext())              //
                                    .load(petInfo.getImageID())              // get image id and load pet image
                                    .into(petImage);                         //
                        } else {
                            PetInfo petInfoTemp = new PetInfo();                 // create a default class of PetInfo
                            editName.setText(petInfoTemp.getName());             // set default pet's  name on screen
                            editType.setText(petInfoTemp.getType());             // set default pet's type on screen
                            editBreed.setText(petInfoTemp.getBreed());           // set default pet's breed on screen
                            editGender.setText(petInfoTemp.getGender());         // set default pet's gender on screen
                            editWeight.setText(petInfoTemp.getWeight() + " kgs");// set default pet's weight on screen
                        }
                    }
                } else {
                    reference.child("Current Pet").setValue("0"); //set value at "(reference) / Current pet "
                    {
                        PetInfo petInfoTemp = new PetInfo();                 // create a default class of PetInfo
                        editName.setText(petInfoTemp.getName());             // set default pet's  name on screen
                        editType.setText(petInfoTemp.getType());             // set default pet's type on screen
                        editBreed.setText(petInfoTemp.getBreed());           // set default pet's breed on screen
                        editGender.setText(petInfoTemp.getGender());         // set default pet's gender on screen
                        editWeight.setText(petInfoTemp.getWeight() + " kgs");// set default pet's weight on screen
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                makeText(PetProfile.this, "cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Return To MainPage Activity on back press
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainPage.class));
        finish();
    }

    //Replace '.' with ','
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}