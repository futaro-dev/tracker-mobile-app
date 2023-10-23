package com.example.trackerapp.ui.item;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.trackerapp.data.database.DatabaseController;
import com.example.trackerapp.utility.ImageHandler;
import com.example.trackerapp.utility.UserController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class ImageController {
    private Context context;
    private String listName, itemName;

    private ProgressDialog progressDialog;


    private String currentUsersName = new UserController().getCurrentUsersDisplayName();
    private String listsPath = ("items/" + currentUsersName + "/lists");
    private String imagesPath = ("images/items/" + currentUsersName + "/lists/");

    private Bitmap bitmap;

    private DatabaseController databaseController = new DatabaseController();

    public ImageController(Context context, String listName, String itemName) {
        this.context = context;
        this.listName = listName;
        this.itemName = itemName;
    }

    public void loadImage(final ImageListener imageListener) {
        try {
            imageListener.onStart();
            File localFile = File.createTempFile("tempFile", ".jpg");
            databaseController.getStorageReference(imagesPath).child(listName + "/" + itemName)
                    .getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            Bitmap tempBitmap = new ImageHandler().getResizedBitmapForPoster(bitmap);
                            imageListener.onSuccess(tempBitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception error) {
                            imageListener.onFailed(error);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading changes...");
            progressDialog.show();

            databaseController.getStorageReference(imagesPath).child(listName + "/" + itemName)
                    .putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            });

        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteImage() {
        databaseController.getStorageReference(imagesPath)
                .child(listName + "/" + itemName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("FirebaseStorageFile", "File successfully deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("FirebaseStorageFile", e.getMessage());
                    }
                });
    }
}
