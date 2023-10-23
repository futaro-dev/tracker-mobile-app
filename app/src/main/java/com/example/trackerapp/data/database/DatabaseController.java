package com.example.trackerapp.data.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DatabaseController {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    public DatabaseReference getDatabaseReference() {
        database = FirebaseDatabase.getInstance("https://tracker-app-aca86-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = database.getReference();
        return databaseReference;
    }

    public DatabaseReference getDatabaseReference(String path) {
        database = FirebaseDatabase.getInstance("https://tracker-app-aca86-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = database.getReference(path);
        return databaseReference;
    }

    public StorageReference getStorageReference() {
        storage = FirebaseStorage.getInstance("gs://tracker-app-aca86.appspot.com");
        storageReference = storage.getReference();

        return storageReference;
    }

    public StorageReference getStorageReference(String path) {
        storage = FirebaseStorage.getInstance("gs://tracker-app-aca86.appspot.com");
        storageReference = storage.getReference(path);

        return storageReference;
    }
}
