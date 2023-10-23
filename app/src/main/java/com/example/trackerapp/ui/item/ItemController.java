package com.example.trackerapp.ui.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.trackerapp.data.database.DatabaseController;
import com.example.trackerapp.data.model.Item;
import com.example.trackerapp.utility.UserController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ItemController {
    private Context context;
    private String listName, itemName;

    private String currentUsersName = new UserController().getCurrentUsersDisplayName();
    private String listsPath = ("items/" + currentUsersName + "/lists");
    private String imagesPath = ("images/items/" + currentUsersName + "/lists/");

    private Bitmap bitmap;

    private DatabaseController databaseController = new DatabaseController();
    private ImageController imageController;

    public ItemController(Context context, String listName, String itemName) {
        this.context = context;
        this.listName = listName;
        this.itemName = itemName;
    }

    public void loadItem(final ItemListener itemListener, final ImageListener imageListener) {
        itemListener.onStart();
        databaseController.getDatabaseReference(listsPath)
                .child(listName + "/" + itemName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemListener.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                itemListener.onFailed(error);
            }
        });

        imageController = new ImageController(context, listName, itemName);
        imageController.loadImage(imageListener);
    }

    public void updateItem(String newStatus, int newProgress, int newScore) {
        databaseController.getDatabaseReference(listsPath)
                .child(listName)
                .orderByChild("name")
                .equalTo(itemName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        deleteItem();
                        Item item = new Item(
                                (currentUsersName + "/" + listName + "/" + itemName),
                                itemName, newStatus, newProgress, newScore
                        );

                        new DatabaseController().getDatabaseReference(listsPath)
                                .child(listName + "/" + itemName)
                                .setValue(item)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Item updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteItem() {
        databaseController.getDatabaseReference(listsPath).child(listName + "/" + itemName).removeValue();
    }

    public void deleteItemWithPicture() {
        databaseController.getDatabaseReference(listsPath).child(listName + "/" + itemName).removeValue();
        imageController = new ImageController(context, listName, itemName);
        imageController.deleteImage();
    }
}
