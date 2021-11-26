package com.example.autofeed.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.autofeed.R;
import com.example.autofeed.classes.PetInfo;
import com.github.sealstudios.fab.FloatingActionButton;
import com.github.sealstudios.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "TAG";
    private CircleImageView petImage;
    private TextView editButton, editName, editType, editBreed, editGender, editWeight;
    private List<String> pets, petsName;
    private String currentPet;
    private FloatingActionButton editPet, changePet, addPet;
    private FloatingActionMenu fabMenu;

    private FirebaseAuth auth;          //authentication
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pet_profile);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        Objects.requireNonNull(getSupportActionBar()).hide();

        setVariables();

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Pets");

        readFireBase();
        setPetImage();

        addPet.setOnClickListener(view -> editDetails());
        editPet.setOnClickListener(view -> editDetails());
        changePet.setOnClickListener(view -> selectPet());

    }

    private void selectPet() {
        // makeText(this, "select pet", Toast.LENGTH_SHORT).show();
        showAlertDialog();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PetProfile.this);
        alertDialog.setTitle("Select Pet");
        String[] items = petsName.toArray(new String[0]);
        final int[] checkedItem = {-1};
        alertDialog.setSingleChoiceItems(items, checkedItem[0], (dialog, which) -> {
            checkedItem[0] = which;
            Log.d(TAG, String.valueOf(checkedItem[0]));
        });

        alertDialog.setPositiveButton("Confirm", (dialog, which) -> {
            if (checkedItem[0] != -1)
                setPetProfile(checkedItem[0]);
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> Log.d(TAG, "simpleAlert: canceled"));

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void setPetProfile(int changePet) {
        reference.child("Current Pet").setValue(String.valueOf(changePet));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PetInfo petInfo = snapshot.child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()))).child(String.valueOf(changePet)).getValue(PetInfo.class);
                if (petInfo != null) {
                    editName.setText(petInfo.getName());
                    editType.setText(petInfo.getType());
                    editBreed.setText(petInfo.getBreed());
                    editGender.setText(petInfo.getGender());
                    editWeight.setText(petInfo.getWeight() + " kgs");
                } else {
                    PetInfo petInfoTemp = new PetInfo();
                    editName.setText(petInfoTemp.getName());
                    editType.setText(petInfoTemp.getType());
                    editBreed.setText(petInfoTemp.getBreed());
                    editGender.setText(petInfoTemp.getGender());
                    editWeight.setText(petInfoTemp.getWeight() + " kgs");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setVariables() {
        petImage = findViewById(R.id.civPetImg);
        editButton = findViewById(R.id.tvEdit);
        editName = findViewById(R.id.tvPetName);
        editType = findViewById(R.id.tvEdit_Pet_Type);
        editBreed = findViewById(R.id.tvEdit_Pet_Breed);
        editGender = findViewById(R.id.tvEdit_Pet_Gender);
        editWeight = findViewById(R.id.tvEdit_Pet_Weight);
        addPet = findViewById(R.id.fabAddPet);
        editPet = findViewById(R.id.fabEditPet);
        changePet = findViewById(R.id.fabChangePet);
        fabMenu = findViewById(R.id.fabMenu);

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

    private void editDetails() {
        petImage.setOnClickListener(view -> setImage());
        Intent intent = new Intent(PetProfile.this, EditPetProfile.class);
        if (pets.isEmpty() || addPet.isPressed()) {
            intent.putExtra("new", "true");
            intent.putExtra("id", String.valueOf(pets.size()));
        } else {
            intent.putExtra("new", "false");
            intent.putExtra("id", pets.get(Integer.parseInt(currentPet)));
        }

        startActivity(intent);
    }

    private void setImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            petImage.setImageURI(selectedImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainPage.class));
        finish();
    }

    private void readFireBase() {

        pets = new ArrayList<>();
        petsName = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pets.clear();
                petsName.clear();
                for (DataSnapshot ds : dataSnapshot.child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()))).getChildren()) {
                    if (ds.exists()) {
                        pets.add(Objects.requireNonNull(ds.child("id").getValue()).toString());
                        petsName.add(Objects.requireNonNull(ds.child("name").getValue()).toString());
                    }
//                    } else {
//                        PetInfo petInfoTemp = new PetInfo("", "", "", "", "", FirebaseDatabase.getInstance().getReference().push().getKey());
//                        ref.child(petInfoTemp.getId()).setValue(petInfoTemp);
//                        Log.d(TAG, String.valueOf(pets));
//                        break;
//                    }
                }
                Log.d(TAG, String.valueOf(pets));
                Log.d(TAG, String.valueOf(petsName));

                currentPet = dataSnapshot.child("Current Pet").getValue(String.class);
                if (pets.size() > 0) {
                    Log.d(TAG, currentPet);
                    if (currentPet != null) {
                        PetInfo petInfo = dataSnapshot.child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()))).child(pets.get(Integer.parseInt(currentPet))).getValue(PetInfo.class);
                        if (petInfo != null) {
                            editName.setText(petInfo.getName());
                            editType.setText(petInfo.getType());
                            editBreed.setText(petInfo.getBreed());
                            editGender.setText(petInfo.getGender());
                            editWeight.setText(petInfo.getWeight() + " kgs");
                        } else {
                            PetInfo petInfoTemp = new PetInfo();
                            editName.setText(petInfoTemp.getName());
                            editType.setText(petInfoTemp.getType());
                            editBreed.setText(petInfoTemp.getBreed());
                            editGender.setText(petInfoTemp.getGender());
                            editWeight.setText(petInfoTemp.getWeight() + " kgs");
                        }
                    }
                } else {
                    reference.child("Current Pet").setValue("0");
                    {
                        PetInfo petInfoTemp = new PetInfo();
                        editName.setText(petInfoTemp.getName());
                        editType.setText(petInfoTemp.getType());
                        editBreed.setText(petInfoTemp.getBreed());
                        editGender.setText(petInfoTemp.getGender());
                        editWeight.setText(petInfoTemp.getWeight() + " kgs");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}