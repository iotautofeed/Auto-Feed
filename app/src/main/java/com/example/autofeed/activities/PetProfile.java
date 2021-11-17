package com.example.autofeed.activities;

import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.autofeed.R;
import com.example.autofeed.activities.EditPetProfile;
import com.example.autofeed.classes.PetInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetProfile extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "TAG";
    private CircleImageView petImage;

    private TextView editButton, editName, editType, editBreed, editGender, editWeight;
    private String updateName, updateType, updateBreed, updateGender, updateWeight;

    private FirebaseAuth auth;          //authentication
    private FirebaseDatabase rootNode;  //real time database
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pet_profile);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        Objects.requireNonNull(getSupportActionBar()).hide();


        setVariables();

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(encodeUserEmail(auth.getCurrentUser().getEmail())).child("Pet Info");
        editButton.setOnClickListener(view -> EditDetails());

        readFireBase();
        setPetImage();
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

    }

    private void EditDetails() {
        petImage.setOnClickListener(view -> setImage());
        startActivity(new Intent(this, EditPetProfile.class));
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
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PetInfo petInfo = dataSnapshot.getValue(PetInfo.class);

                if (petInfo != null) {
                    editName.setText(petInfo.getName());
                    editType.setText(petInfo.getType());
                    editBreed.setText(petInfo.getBreed());
                    editGender.setText(petInfo.getGender());
                    editWeight.setText(petInfo.getWeight());
                } else {
                    PetInfo petInfoTemp = new PetInfo();
                    editName.setText(petInfoTemp.getName());
                    editType.setText(petInfoTemp.getType());
                    editBreed.setText(petInfoTemp.getBreed());
                    editGender.setText(petInfoTemp.getGender());
                    editWeight.setText(petInfoTemp.getWeight());
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

}