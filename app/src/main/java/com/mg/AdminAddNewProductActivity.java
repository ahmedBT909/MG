package com.mg;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mg.databinding.ActivityAdminAddNewProductBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private ActivityAdminAddNewProductBinding binding;
    private static final String TAG = "AD_CREATE_TAG";
    private ProgressDialog progressDialog;
    private Uri imageUri = null;
    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private AdapterImagePicked adapterImagePicked;

    //مهم
    private boolean isEditMode = false;
    private String adIdForEditing = "";

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    private String CategoryName, productRandomKey, saveCurrentDate, saveCurrentTime;

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Are you sure?", Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(mRunnable, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddNewProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        CategoryName = getIntent().getExtras().get("category").toString();


        //مهم
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);
        Log.d(TAG, "onCreate: isEditMode: " + isEditMode);

        binding.addNewProduct.setText("Product Ad");

        imagePickedArrayList = new ArrayList<>();
        loadImage();

        binding.backProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.camId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickOptions();
            }
        });

        binding.addNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }


    private void loadImage() {
        Log.e(TAG, "loadImage: ");

        adapterImagePicked = new AdapterImagePicked(this, imagePickedArrayList, adIdForEditing);
        binding.selectProductImage.setAdapter(adapterImagePicked);
    }

    private void showImagePickOptions() {
        Log.e(TAG, "showImagePickOptions: ");

        PopupMenu popupMenu = new PopupMenu(this, binding.camId);

        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == 1) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        String[] cameraPermissions = new String[]{Manifest.permission.CAMERA};
                        requestCameraPermissions.launch(cameraPermissions);
                    } else {
                        String[] cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestCameraPermissions.launch(cameraPermissions);
                    }

                } else if (itemId == 2) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pickImageGallery();
                    } else {
                        String storagePermissions = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        requestStoragePermission.launch(storagePermissions);
                    }
                }
                return true;
            }
        });
    }

    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.e(TAG, "onActivityResult: isGranted: " + isGranted);

                    if (isGranted) {
                        pickImageGallery();

                    } else {
                        Utils.toast(AdminAddNewProductActivity.this, "Storage Permission denied...");
                    }

                }
            }
    );

    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.e(TAG, "onActivityResult: ");
                    Log.e(TAG, "onActivityResult: " + result.toString());

                    boolean areAllGranted = true;
                    for (Boolean isGranted : result.values()) {
                        areAllGranted = areAllGranted && isGranted;
                    }

                    if (areAllGranted) {
                        pickImageCamera();
                    } else {
                        Utils.toast(AdminAddNewProductActivity.this, "Camera or Storage or both permissions denied...");
                    }
                }
            }
    );

    private void pickImageCamera() {
        Log.e(TAG, "pickImageCamera: ");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMPORARY_IMAGE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMPORARY_IMAGE_DESCRIPTION");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private void pickImageGallery() {
        Log.e(TAG, "pickImageGallery: ");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult: ");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.e(TAG, "onActivityResult: imageUri" + imageUri);
                        String timestamp = "" + Utils.getTimestamp();
                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);
                        loadImage();
                    } else {

                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult: ");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.e(TAG, "onActivityResult: imageUri" + imageUri);
                        String timestamp = "" + Utils.getTimestamp();
                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);
                        loadImage();
                    } else {
                    }
                }
            }
    );

    private String nameProduct = "";
    private String priceProduct = "";
    private String descriptionProduct = "";
    private String quntityProduct = "";


    private void validateData() {
        Log.e(TAG, "validateData: ");

        nameProduct = binding.productName.getText().toString().trim();
        priceProduct = binding.productPrice.getText().toString().trim();
        quntityProduct = binding.productQuntity.getText().toString().trim();
        descriptionProduct = binding.productDescription.getText().toString().trim();


        if (imagePickedArrayList.isEmpty()) {
            Utils.toast(this, "Pick at-least one image");
        } else if (nameProduct.isEmpty()) {
            binding.productName.setError("Enter Title");
            binding.productName.requestFocus();
        } else if (quntityProduct.isEmpty()) {
            binding.productQuntity.setError("Enter your phone number");
            binding.productQuntity.requestFocus();
        } else if (priceProduct.isEmpty()) {
            binding.productPrice.setError("Invalid Number");
            binding.productPrice.requestFocus();
        } else if (descriptionProduct.isEmpty()) {
            binding.productDescription.setError("Enter Description");
            binding.productDescription.requestFocus();
        } else {
            postAd();
        }


    }


    private void postAd() {
        Log.e(TAG, "postAd: ");
        progressDialog.setMessage("Publishing Ad");
        progressDialog.show();
        long timestamp = Utils.getTimestamp();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Products");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pid", productRandomKey);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("pname", nameProduct);
        hashMap.put("price", priceProduct);
        hashMap.put("Quntity", quntityProduct);
        hashMap.put("description", descriptionProduct);
        hashMap.put("category", CategoryName);

        refAds.child(productRandomKey)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "onSuccess: Ad Published");
                        uploadImagesStorage(productRandomKey);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        Utils.toast(AdminAddNewProductActivity.this, "Failed to publish Ad due to " + e.getMessage());
                    }
                });

    }

    private void uploadImagesStorage(String adId) {
        Log.e(TAG, "uploadImagesStorage: ");

        for (int i = 0; i < imagePickedArrayList.size(); i++) {
            ModelImagePicked modelImagePicked = imagePickedArrayList.get(i);

            if (!modelImagePicked.getFromInternet()) {
                String imageName = modelImagePicked.getId();
                String filePathAndName = "Products/" + imageName;

                int imageIndexForProgress = i + 1;

                StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

                storageReference.putFile(modelImagePicked.getImageUri())
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                                String message = "Uploading " + imageIndexForProgress + " of " + imagePickedArrayList.size() + " images...\nProgress " + (int) progress + "%";

                                Log.e(TAG, "onProgress: message" + message);
                                progressDialog.setMessage(message);
                                progressDialog.show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.e(TAG, "onSuccess: ");
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                                while (!uriTask.isSuccessful()) ;
                                Uri uploadedImageUrl = uriTask.getResult();

                                if (uriTask.isSuccessful()) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("id", "" + modelImagePicked.getId());
                                    hashMap.put("imageUrl", "" + uploadedImageUrl);

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products");
                                    ref.child(adId).child("Images")
                                            .child(imageName)
                                            .updateChildren(hashMap);
                                }
                                progressDialog.dismiss();
                                startActivity(new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: ", e);
                                progressDialog.dismiss();
                            }
                        });
            } else {

            }
        }
    }
}