package com.example.trackerapp.utility;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.trackerapp.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserController {
    private FirebaseAuth auth;

    public String getCurrentUsersDisplayName() {
        auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser().getDisplayName();
    }
}
