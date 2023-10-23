package com.example.trackerapp.ui.item;

import android.graphics.Bitmap;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FileDownloadTask;

public interface ImageListener {
    public void onStart();
    public void onSuccess(Bitmap bitmap);
    public void onFailed(Exception error);
}
