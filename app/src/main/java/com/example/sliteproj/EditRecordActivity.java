package com.example.sliteproj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditRecordActivity extends AppCompatActivity {

    private ImageView pImageView;
    private EditText pNameEt, pAgeEt, pPhoneEt, pDesEt;
    Button saveInfoBt;
    ActionBar actionBar;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri imageUri;

    private String id, name, age, phone, des , addTimeStamp, updateTimeStamp;
    private boolean editMode = false;
    private DatabaseHelper dbHelper;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        pd = new ProgressDialog(EditRecordActivity.this);

        pImageView = findViewById(R.id.personImage);
        pNameEt = findViewById(R.id.personName);
        pAgeEt = findViewById(R.id.personAge);
        pPhoneEt = findViewById(R.id.personPhone);
        pDesEt = findViewById(R.id.personDes);
        saveInfoBt = findViewById(R.id.addInfoBtn);

        Intent intent = getIntent();
        editMode = intent.getBooleanExtra("editMode", false);
        id = intent.getStringExtra("ID");
        name = intent.getStringExtra("NAME");
        age = intent.getStringExtra("AGE");
        phone = intent.getStringExtra("PHONE");
        des = intent.getStringExtra("DES");
        imageUri = Uri.parse(intent.getStringExtra("IMAGE"));
        addTimeStamp = intent.getStringExtra("ADD_TIMESTAMP");
        updateTimeStamp = intent.getStringExtra("UPDATE_TIMESTAMP");

        if (editMode) {

            // update data
            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("NAME");
            age = intent.getStringExtra("AGE");
            phone = intent.getStringExtra("PHONE");
            des = intent.getStringExtra("DES");
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"));
            addTimeStamp = intent.getStringExtra("ADD_TIMESTAMP");
            updateTimeStamp = intent.getStringExtra("UPDATE_TIMESTAMP");

            pNameEt.setText(name);
            pAgeEt.setText(age);
            pPhoneEt.setText(phone);
            pDesEt.setText(des);

            if (imageUri.toString().equals("null")) {
                pImageView.setImageResource(R.drawable.ic_action_addphoto);
            }
            else {
                pImageView.setImageURI(imageUri);
            }
        }
        else {
            actionBar.setTitle("Add Information");
            // add data
        }


        dbHelper = new DatabaseHelper(this);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickDialog();

            }
        });

        saveInfoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("Updating");
                pd.setMessage("Please wait...");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                getData();
                startActivity(new Intent(EditRecordActivity.this, MainActivity.class));
                Toast.makeText(EditRecordActivity.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {

        name = ""+pNameEt.getText().toString().trim();
        age = ""+pAgeEt.getText().toString().trim();
        phone = ""+pPhoneEt.getText().toString().trim();
        des = ""+pDesEt.getText().toString().trim();

        if (editMode) {

            String newUpdatedTime = ""+System.currentTimeMillis();
            dbHelper.updateInfo(
                    ""+id,
                    ""+imageUri,
                    ""+name,
                    ""+age,
                    ""+phone,
                    ""+des,
                    ""+addTimeStamp,
                    ""+updateTimeStamp
            );
        }
        else {

            String timeStamp = ""+System.currentTimeMillis();

            dbHelper.insertInfo(
                    ""+imageUri,
                    ""+name,
                    ""+age,
                    ""+phone,
                    ""+des,
                    ""+addTimeStamp,
                    ""+updateTimeStamp);

        }
    }

    private void imagePickDialog() {

        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }

            }
        });

        builder.create().show();
    }

    private void pickFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkStoragePermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {

        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case CAMERA_REQUEST_CODE: {

                if (grantResults.length>0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    }

                    else {
                        Toast.makeText(this, "Camera permission required!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
            break;
            case STORAGE_REQUEST_CODE:{

                if (grantResults.length>0) {

                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(this, "Storage permission required!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }

            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }

            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    imageUri = resultUri;
                    pImageView.setImageURI(resultUri);
                }

                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Exception error = result.getError();
                    Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
