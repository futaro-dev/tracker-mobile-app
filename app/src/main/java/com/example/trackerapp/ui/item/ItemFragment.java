package com.example.trackerapp.ui.item;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackerapp.R;
import com.example.trackerapp.data.model.Item;
import com.example.trackerapp.databinding.FragmentItemBinding;
import com.example.trackerapp.databinding.FragmentItemsBinding;
import com.example.trackerapp.utility.ImageHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class ItemFragment extends Fragment {
    private FragmentItemBinding binding;

    private ProgressDialog progressDialog;

    private TextView name, status, progress, score;
    private ImageView image;
    private Button removeItemButton, editItemButton;

    private ItemController itemController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentItemBinding.inflate(inflater, container, false);
        String[] parts = getArguments().getString("itemId").split("/");
        String listName = parts[1];
        String itemName = parts[2];

        name = binding.itemInfoName;
        status = binding.itemInfoStatus;
        progress = binding.itemInfoProgress;
        score = binding.itemInfoScore;
        image = binding.itemInfoImage;

        itemController = new ItemController(getContext(), listName, itemName);
        itemController.loadItem(new ItemListener() {
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
            public void onSuccess(DataSnapshot data) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    Item item = data.getValue(Item.class);
                    binding.itemInfoName.setText(item.getName().substring(0, 1).toUpperCase() + item.getName().substring(1));
                    binding.itemInfoStatus.setText(item.getStatus());
                    binding.itemInfoProgress.setText(Integer.toString(item.getProgress()));
                    binding.itemInfoScore.setText(Integer.toString(item.getScore()));
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        }, new ImageListener() {
            @Override
            public void onStart() {}

            @Override
            public void onSuccess(Bitmap bitmap) {
                binding.itemInfoImage.setImageBitmap(bitmap);
                progressDialog.dismiss();
            }

            @Override
            public void onFailed(Exception error) {
                progressDialog.dismiss();
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("itemId", getArguments().getString("itemId"));
                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_editItemImageFragment, bundle);
            }
        });

        editItemButton = binding.itemEditButton;
        editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("itemId", getArguments().getString("itemId"));
                bundle.putString("progress", progress.getText().toString());
                bundle.putString("status", status.getText().toString());
                bundle.putString("score", score.getText().toString());
                Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_editItemFragment, bundle);
            }
        });

        removeItemButton = binding.itemRemoveButton;
        removeItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Remove Item")
                        .setMessage("Are you sure you want to remove this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemController.deleteItemWithPicture();
                                Toast.makeText(getContext(), "Item removed successfully", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).popBackStack();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                builder.show();
            }
        });

        return binding.getRoot();
    }
}