package com.example.trackerapp.ui.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackerapp.MainActivity;
import com.example.trackerapp.R;
import com.example.trackerapp.data.model.User;
import com.example.trackerapp.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;

    private EditText email, username, password;
    private Button register;
    private TextView loginText;
    private boolean check;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        mDatabase = FirebaseDatabase.getInstance("https://tracker-app-aca86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference();
        auth = FirebaseAuth.getInstance();

        email = binding.registerEmail;
        username = binding.registerUsername;
        password = binding.registerPassword;
        register = binding.registerButton;
        loginText = binding.registerLoginText;

        progressDialog = new ProgressDialog(getContext());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });

        return binding.getRoot();
    }

    private void register() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String userEmail = email.getText().toString().trim().toLowerCase();
        String userUsername = username.getText().toString().trim().toLowerCase();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userUsername.isEmpty() || userPassword.isEmpty()) {
            if (userEmail.isEmpty()) {
                email.setError("Email cannot be empty");
            }

            if (userUsername.isEmpty()) {
                username.setError("Username cannot be empty");
            }

            if (userPassword.isEmpty()) {
                password.setError("Password cannot be empty");
            }
        } else if (userPassword.length() < 6) {
            password.setError("Password is too short");
        } else {
            mDatabase
                    .child("users")
                    .orderByChild("username")
                    .equalTo(userUsername)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        progressDialog.dismiss();
                        username.setError("Username already exists");
                    } else {
                        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User(auth.getCurrentUser().getUid(), userEmail, userUsername);

                                // The following code sets the display name of the user to the username they have entered when registering.
                                // This is so the built in Firebase function getDisplayName() can be called on the user that is currently
                                // logged in.
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userUsername)
                                                .build();

                                firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("UserProfile", "User's username has been set");
                                        }
                                    }
                                });

                                // The following code adds a new entry into the Firebase realtime database with the user's data.
                                mDatabase
                                        .child("users")
                                        .child(userUsername)
                                        .setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                                            Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_loginFragment);
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(
                                                    getContext(),
                                                    "Registration Failed" + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}