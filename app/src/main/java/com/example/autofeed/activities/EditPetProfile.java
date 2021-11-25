package com.example.autofeed.activities;

import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.autofeed.classes.PetInfo;
import com.example.autofeed.R;
import com.example.autofeed.classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPetProfile extends AppCompatActivity {

    private static final String TAG = "aaa";
    private static final int TAKE_IMAGE_CODE = 1;
    private static final int CAMERA_REQUEST = 0;
    private static final int PICK_IMAGE = 1;

    private FirebaseAuth auth;          //authentication
    private FirebaseDatabase rootNode;  //real time database
    private DatabaseReference reference, reference1;

    private CircleImageView petImage;
    private TextInputEditText name, type, breed, gender, weight;
    private FloatingActionButton confirm;
    private String updateName, updateType, updateBreed, updateGender, updateWeight, id;
    private String isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_edit_pet_profile);
        Intent intent = getIntent();
        isNew = intent.getStringExtra("new");
        id = intent.getStringExtra("id");
        Log.d(TAG,isNew + " " + id);

        setVariables();

        auth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Pets").child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail())));
        reference1 = FirebaseDatabase.getInstance().getReference().child("Pets").child("Current Pet");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
//        if (user.getPhotoUrl() != null) {
//            Glide.with(this)
//                    .load(user.getPhotoUrl())
//                    .into(petImage);
//        }
        petImage.setOnClickListener(view -> addPetImage());
        confirm.setOnClickListener(view -> updateProfile());
    }

    private Boolean check() {
        if (updateName.isEmpty() || updateType.isEmpty() || updateBreed.isEmpty() || updateGender.isEmpty() || updateWeight.isEmpty()) {
            makeText(this, "Enter all Details", Toast.LENGTH_SHORT).show();
            return false;
        }
        makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
        return true;

    }

    private void updateProfile() {

        updateName = Objects.requireNonNull(name.getText()).toString().trim();
        updateType = Objects.requireNonNull(type.getText()).toString().trim();
        updateBreed = Objects.requireNonNull(breed.getText()).toString().trim();
        updateGender = Objects.requireNonNull(gender.getText()).toString().trim();
        updateWeight = Objects.requireNonNull(weight.getText()).toString().trim();
        if (check()) {
            PetInfo petInfo = new PetInfo(updateName, updateType, updateBreed, updateGender, updateWeight, id);

            saveDateFireBase(petInfo);
            Intent intent = new Intent(EditPetProfile.this, PetProfile.class);
            startActivity(intent);
            finish();
        }
    }

    private void setVariables() {
        petImage = findViewById(R.id.civPetImg);
        name = findViewById(R.id.etPet_Name);
        type = findViewById(R.id.etPet_Type);
        breed = findViewById(R.id.etPet_Breed);
        gender = findViewById(R.id.etPet_Gender);
        weight = findViewById(R.id.etPet_Weight);
        confirm = findViewById(R.id.fabConfirm_Edit);
    }

    private void saveDateFireBase(PetInfo petInfo) {
        if(isNew.equals("true"))
            petInfo.setId(id);
        Log.d(TAG, petInfo.getId());
        reference.child(petInfo.getId()).setValue(petInfo);
        reference1.setValue(id);
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public void addPetImage() {
        String title, message, posButtonTxt, negButtonTxt, neutralButtonTxt;

        title = "Upload Image";
//        message = "select";
        posButtonTxt = "Camera";
        negButtonTxt = "Gallery";
        neutralButtonTxt = "Cancel";
        simpleAlert(title,false, posButtonTxt, negButtonTxt, neutralButtonTxt);
    }

    public void simpleAlert(String title, boolean cancelable,
                            String posButtonTxt, String negButtonTxt, String neutralButtonTxt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
//        builder.setMessage(message);
        builder.setCancelable(cancelable);

        builder.setPositiveButton(posButtonTxt, (dialog, which) -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });
        builder.setNegativeButton(negButtonTxt, (dialog, which) -> {
            Intent galleryIntent = new Intent().setType("image/*").setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(galleryIntent, negButtonTxt), PICK_IMAGE);
        });
        builder.setNeutralButton(neutralButtonTxt, (dialog, which) ->
                Log.d(TAG, "simpleAlert: canceled"));

        builder.create();
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                petImage.setImageBitmap(bitmap);
                handleUpload(bitmap);
            }
        } else if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            if (resultCode == RESULT_OK) {
                Uri imagePath = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                petImage.setImageBitmap(bitmap);
                assert bitmap != null;
                handleUpload(bitmap);
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid + ".jpeg");

        storageReference.putBytes(baos.toByteArray())
                .addOnSuccessListener(taskSnapshot -> getDownloadUrl(storageReference))
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e.getCause()));
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d(TAG, "onSuccess: " + uri);
                    setUserProfileUrl(uri);
                });
    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        assert user != null;
        user.updateProfile(request)
                .addOnSuccessListener(aVoid -> makeText(this, "Updated succesfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> makeText(this, "Profile image failed...", Toast.LENGTH_SHORT).show());
    }
}