package com.example.trackerapp.ui.edit;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trackerapp.R;
import com.example.trackerapp.databinding.FragmentEditItemBinding;
import com.example.trackerapp.databinding.FragmentEditItemImageBinding;
import com.example.trackerapp.ui.item.ImageController;
import com.example.trackerapp.ui.item.ImageListener;
import com.example.trackerapp.ui.item.ItemController;
import com.example.trackerapp.utility.ImageHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class EditItemImageFragment extends Fragment {
    private FragmentEditItemImageBinding binding;

    private static final int CAMERA_REQUEST = 200;

    private ImageController imageController;
    private ProgressDialog progressDialog;

    private String listName, itemName;

    private Uri imageUri;

    private ImageView tempImage;
    private Button selectImageFromGalleryButton, selectImageFromCameraButton, uploadButton, cancelButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditItemImageBinding.inflate(inflater, container, false);

        String[] parts = getArguments().getString("itemId").split("/");
        listName = parts[1];
        itemName = parts[2];

        imageController = new ImageController(getContext(), listName, itemName);
        imageController.loadImage(new ImageListener() {
            @Override
            public void onStart() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Loading data...");
                    progressDialog.setIndeterminate(true);
                }

                progressDialog.show();
            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                binding.itemTempImage.setImageBitmap(bitmap);
                progressDialog.dismiss();
            }

            @Override
            public void onFailed(Exception error) {
                progressDialog.dismiss();
            }
        });

        tempImage = binding.itemTempImage;
        selectImageFromGalleryButton = binding.selectImageFromGalleryButton;
        selectImageFromCameraButton = binding.selectImageFromCameraButton;
        uploadButton = binding.uploadImageButton;
        cancelButton = binding.cancelButton;

        selectImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery();
            }
        });

        selectImageFromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromCamera();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageController.uploadImage(imageUri);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        return binding.getRoot();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        galleryResultLauncher.launch(intent);
    }

    private void selectImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.CAMERA
            }, CAMERA_REQUEST);
        }
        cameraResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        tempImage.setImageURI(imageUri);
                        Bitmap bitmap = ((BitmapDrawable) tempImage.getDrawable()).getBitmap();
                        Bitmap resizedBitmap = new ImageHandler().getResizedBitmapForPoster(bitmap);
                        tempImage.setImageBitmap(resizedBitmap);
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap bitmap = (Bitmap) extras.get("data");

                        imageUri = getImageUri(bitmap);
                        tempImage.setImageURI(imageUri);
                        Bitmap resizedBitmap = new ImageHandler().getResizedBitmapForPoster(bitmap);
                        tempImage.setImageBitmap(resizedBitmap);
                    }
                }
            }
    );

    public Uri getImageUri(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), image, "title", null);
        return Uri.parse(path);
    }
}