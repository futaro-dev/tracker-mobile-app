package com.example.trackerapp.ui.items;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.trackerapp.R;
import com.example.trackerapp.data.model.Item;
import com.example.trackerapp.data.model.ItemList;
import com.example.trackerapp.databinding.FragmentItemsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ItemsFragment extends Fragment {
    private FragmentItemsBinding binding;

    private RecyclerView itemRecyclerView;
    private FloatingActionButton addItemButton;
    private ItemsAdapter itemsAdapter;
    private List<Item> items = new ArrayList<>();

    private String status;
    private int score;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentItemsBinding.inflate(inflater, container, false);

        mDatabase = FirebaseDatabase.getInstance("https://tracker-app-aca86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference();
        mAuth = FirebaseAuth.getInstance();

        itemRecyclerView = binding.itemRecyclerView;
        itemRecyclerView.setHasFixedSize(true);
        addItemButton = binding.addItemButton;

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_add_item);

                EditText itemName = dialog.findViewById(R.id.dialogItemName);
                Spinner itemStatus = dialog.findViewById(R.id.dialogItemStatus);
                EditText itemProgress = dialog.findViewById(R.id.dialogItemProgress);
                Spinner itemScore = dialog.findViewById(R.id.dialogItemScore);

                ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                        getContext(), R.array.status, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                );
                statusAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                itemStatus.setAdapter(statusAdapter);

                itemStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        status = adapterView.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                ArrayAdapter<CharSequence> scoreAdapter = ArrayAdapter.createFromResource(
                        getContext(), R.array.score, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                );
                scoreAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                itemScore.setAdapter(scoreAdapter);

                itemScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i > 0) {
                            score = Math.abs(i - 10) + 1;
                        } else {
                            score = 0;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


                Button addItemButton = dialog.findViewById(R.id.dialogAddItemButton);

                addItemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemName.getText().toString().isEmpty() || itemProgress.getText().toString().isEmpty()) {
                            if (itemName.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
                            }

                            if (itemProgress.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Progress cannot be blank", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            saveNewItem(
                                    itemName.getText().toString().toLowerCase().trim(),
                                    status,
                                    Integer.parseInt(itemProgress.getText().toString().trim()),
                                    score
                            );
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });

        itemsAdapter = new ItemsAdapter();
        itemRecyclerView.setAdapter(itemsAdapter);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadItems();

        return binding.getRoot();
    }

    private void loadItems() {
         mDatabase
                .child("items")
                .child(mAuth.getCurrentUser().getDisplayName())
                .child("lists")
                .child(getArguments().getString("listName"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        items.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            item.setName(item.getName().substring(0, 1).toUpperCase() + item.getName().substring(1));
                            items.add(item);
                        }
                        itemsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        itemsAdapter.setData(items);
    }

    private void saveNewItem(String name, String status, int progress, int score) {
        mDatabase
                .child("items")
                .child(mAuth.getCurrentUser().getDisplayName())
                .child("lists")
                .child(getArguments().getString("listName"))
                .orderByChild("name")
                .equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(getContext(), "You already have an item with that name", Toast.LENGTH_SHORT).show();
                } else {
                    Item item = new Item(
                            (mAuth.getCurrentUser().getDisplayName() + "/" + getArguments().getString("listName") + "/" + name),
                            name, status, progress, score
                    );
                    mDatabase
                            .child("items")
                            .child(mAuth.getCurrentUser().getDisplayName())
                            .child("lists")
                            .child(getArguments().getString("listName"))
                            .child(name)
                            .setValue(item)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Item created successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
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