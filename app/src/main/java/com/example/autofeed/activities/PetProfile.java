package com.example.autofeed.activities;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton addPet;
    private TextView editButton, editName, editType, editBreed, editGender, editWeight;
    private List<String> pets;

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
        editButton.setOnClickListener(view -> editDetails());

    }

    private void addNewPet() {
//        Intent intent = new Intent(PetProfile.this, EditPetProfile.class);
//        intent.putExtra("new", "true");
//        intent.putExtra("id","null");
//        startActivity(intent);

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

    private void setVariables() {
        petImage = findViewById(R.id.civPetImg);
        editButton = findViewById(R.id.tvEdit);
        editName = findViewById(R.id.tvPetName);
        editType = findViewById(R.id.tvEdit_Pet_Type);
        editBreed = findViewById(R.id.tvEdit_Pet_Breed);
        editGender = findViewById(R.id.tvEdit_Pet_Gender);
        editWeight = findViewById(R.id.tvEdit_Pet_Weight);
        addPet = findViewById(R.id.fabAddPet);

    }

    private void editDetails() {
        petImage.setOnClickListener(view -> setImage());
        Intent intent = new Intent(PetProfile.this, EditPetProfile.class);
        if (pets.isEmpty() || addPet.isPressed()) {
            intent.putExtra("new", "true");
            intent.putExtra("id", "null");
        } else {
            intent.putExtra("new", "false");
            intent.putExtra("id", pets.get(0));
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

        DatabaseReference ref = reference.child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail())));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pets.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {
                        pets.add(Objects.requireNonNull(ds.child("id").getValue()).toString());
                    }
//                    } else {
//                        PetInfo petInfoTemp = new PetInfo("", "", "", "", "", FirebaseDatabase.getInstance().getReference().push().getKey());
//                        ref.child(petInfoTemp.getId()).setValue(petInfoTemp);
//                        Log.d(TAG, String.valueOf(pets));
//                        break;
//                    }
                }
                Log.d(TAG, String.valueOf(pets));

                if (pets.size() > 0) {
                    PetInfo petInfo = dataSnapshot.child(pets.get(0)).getValue(PetInfo.class);

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