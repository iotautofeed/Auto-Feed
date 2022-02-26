package com.example.autofeed.activities;

import static android.widget.Toast.makeText;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autofeed.R;
import com.example.autofeed.classes.PetInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPetProfile extends AppCompatActivity {

    private static final String TAG = "aaa";
    private static final int TAKE_IMAGE_CODE = 1;
    private static final int CAMERA_REQUEST = 0;                 // define int variable
    private static final int PICK_IMAGE = 1;                     // define int variable

    private FirebaseAuth auth;                                   // define variable for FireBase authentication
    private DatabaseReference reference, reference1;             // define variable for access realtime database

    private CircleImageView petImage;                            // define CircleImageView variable for pet image
    private TextInputEditText name, type, breed, gender, weight; // define variables for text input
    private FloatingActionButton confirm;                        //define FloatingActionButton variable for button
    private String updateName, updateType, updateBreed,          // define string variables
            updateGender, updateWeight, id, imageId,isNew;       //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();  // hide action bar
        setContentView(R.layout.activity_edit_pet_profile);

        Intent intent = getIntent();                // create an Intent variable that get the data sent from PetProfile's intent
        isNew = intent.getStringExtra("new"); // retrieve extended data from the intent in "new"
        id = intent.getStringExtra("id");     // retrieve extended data from the intent in "id"
        Log.d(TAG, isNew + " " + id);

        setVariables(); // function for variables setup

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Pets").child(encodeUserEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail())));
        reference1 = FirebaseDatabase.getInstance().getReference().child("Pets").child("Current Pet");

        petImage.setOnClickListener(view -> addPetImage());
        confirm.setOnClickListener(view -> updateProfile());
    }

    private void setVariables() {                    // function for linking the UI to the code
        petImage = findViewById(R.id.civPetImg);     //
        name = findViewById(R.id.etPet_Name);        //
        type = findViewById(R.id.etPet_Type);        //
        breed = findViewById(R.id.etPet_Breed);      //
        gender = findViewById(R.id.etPet_Gender);    //
        weight = findViewById(R.id.etPet_Weight);    //
        confirm = findViewById(R.id.fabConfirm_Edit);//
    }

    private Boolean check() { // function for pet's credentials confirmation
        if (updateName.isEmpty() || updateType.isEmpty() || updateBreed.isEmpty() || updateGender.isEmpty() || updateWeight.isEmpty()) { // check if one of the credentials is missing
            makeText(this, "Enter all Details", Toast.LENGTH_SHORT).show(); // notify the user via  pop message
            return false;                                                               // exit current function and return false
        }
        makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show(); // notify the user via  pop message
        return true;                                                              //  exit current function and return false

    }

    private void updateProfile() {                                                 // function for update pet's credentials

        updateName = Objects.requireNonNull(name.getText()).toString().trim();     //
        updateType = Objects.requireNonNull(type.getText()).toString().trim();     //
        updateBreed = Objects.requireNonNull(breed.getText()).toString().trim();   // store pet's credentials the user entered
        updateGender = Objects.requireNonNull(gender.getText()).toString().trim(); //
        updateWeight = Objects.requireNonNull(weight.getText()).toString().trim(); //
        if (check()) {                                                             // execute chuck() function . if return true update pet's credentials
            PetInfo petInfo = new PetInfo(updateName, updateType, updateBreed, updateGender, updateWeight, id, imageId); // create a new PetInfo class that store pet's credentials

            saveDataFireBase(petInfo);                                                      // pass the petInfo class to a function that upload the data to firebase
            startActivity(new Intent(EditPetProfile.this, PetProfile.class));  // start PetProfile activity
            finish();                                                                       //  and finish current activity
        }
    }

    private void saveDataFireBase(PetInfo petInfo) {        // function that update pet's data
        if (isNew.equals("true"))                           // if the user creates new pet
            petInfo.setId(id);                              // set the pet new id
        Log.d(TAG, petInfo.getId());                        //
        reference.child(petInfo.getId()).setValue(petInfo); // update pets data in the location "(reference) / (pet's id)" in firebase
        reference1.setValue(id);                            // set updated pet as current pet
    }

    public void addPetImage() {                                     // function for choosing image source
        String title, message, posButtonTxt, negButtonTxt, neutralButtonTxt; // create string variables
        title = "Upload Image";       //
//        message = "select";
        posButtonTxt = "Camera";     // set variables content
        negButtonTxt = "Gallery";    //
        neutralButtonTxt = "Cancel"; //
        simpleAlert(title, false, posButtonTxt, negButtonTxt, neutralButtonTxt); // execute a function for creating alert dialog
    }

    public void simpleAlert(String title, boolean cancelable, String posButtonTxt, String negButtonTxt, String neutralButtonTxt) { // function for creating alert dialog and image source select
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // creates a builder for an alert dialog
        builder.setTitle(title);                                             // set the title displayed in the dialog
        builder.setCancelable(cancelable);                                   // set whether the dialog is cancelable or not. Default is true.
        //        builder.setMessage(message);

        builder.setPositiveButton(posButtonTxt, (dialog, which) -> {           // set a listener to be invoked when the positive button of the dialog is pressed
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // get access to camera to take a picture of the pet
            startActivityForResult(cameraIntent, CAMERA_REQUEST);              //
        });
        builder.setNegativeButton(negButtonTxt, (dialog, which) -> {                               // set a listener to be invoked when the positive button of the dialog is pressed
            Intent galleryIntent = new Intent().setType("image/*").setAction(Intent.ACTION_PICK);  //get access to gallery to select a picture of the pet
            startActivityForResult(Intent.createChooser(galleryIntent, negButtonTxt), PICK_IMAGE); //
        });
        builder.setNeutralButton(neutralButtonTxt, (dialog, which) ->
                Log.d(TAG, "simpleAlert: canceled"));

        builder.create();
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //     Upload Image & Create Url

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && data != null) {          // check if the user chose to open camera
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                petImage.setImageBitmap(bitmap);
                handleUpload(bitmap);
            }
        } else if (requestCode == PICK_IMAGE && data != null && data.getData() != null) { // check if the user chose to open gallery
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

    private void handleUpload(Bitmap bitmap) { //

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // create a ByteArrayOutputStream class. This class implements an output stream in which the data is written into a byte array
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // Write a compressed version of the bitmap to the specified outputstream.

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(); // create a string variable to hold the user's Uid
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()     //
                .child("profileImages")                                                            //create a reference to a Google Cloud Storage object.
                .child(uid + ".jpeg");                                                             //

        storageReference.putBytes(baos.toByteArray())                                     // upload image to firebase
                .addOnSuccessListener(taskSnapshot -> getDownloadUrl(storageReference))   // add listener if upload succeeded execute a function that get the image url
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e.getCause())); // add listener if upload failed
    }

    private void getDownloadUrl(StorageReference reference) { // function that get the image url
        reference.getDownloadUrl()                            // get image url from storage reference
                .addOnSuccessListener(uri -> {                //
                    Log.d(TAG, "onSuccess: " + uri);
                    imageId = String.valueOf(uri);           // save the url in imageId
                    setUserProfileUrl(uri);                  // execute a function
                });
    }

    private void setUserProfileUrl(Uri uri) {                                    // function
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();         //

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()//
                .setPhotoUri(uri)                                                //
                .build();                                                        //

        assert user != null;                                                                                                   //
        user.updateProfile(request)                                                                                            //
                .addOnSuccessListener(aVoid -> makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show()) // add listener if upload succeeded
                .addOnFailureListener(e -> makeText(this, "Profile image failed...", Toast.LENGTH_SHORT).show()); // add listener if upload failed
    }

    //Replace '.' with ','
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}