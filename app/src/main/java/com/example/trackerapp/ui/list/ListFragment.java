package com.example.trackerapp.ui.list;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trackerapp.R;
import com.example.trackerapp.data.model.Item;
import com.example.trackerapp.data.model.ItemList;
import com.example.trackerapp.databinding.FragmentListBinding;
import com.example.trackerapp.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    private FragmentListBinding binding;

    private RecyclerView listRecyclerView;
    private FloatingActionButton addListButton;
    private ListAdapter listAdapter;
    private List<ItemList> itemList;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);

        mDatabase = FirebaseDatabase.getInstance("https://tracker-app-aca86-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference();
        mAuth = FirebaseAuth.getInstance();

        listRecyclerView = binding.listRecyclerView;
        listRecyclerView.setHasFixedSize(true);
        addListButton = binding.addListButton;

        itemList = new ArrayList<>();

        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_add_list);

                EditText listName = dialog.findViewById(R.id.dialogListName);
                Button addListButton = dialog.findViewById(R.id.dialogAddListButton);

                addListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listName.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            saveNewList(listName.getText().toString().toLowerCase().trim());
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });

        listAdapter = new ListAdapter(getContext(), mDatabase, mAuth);
        listRecyclerView.setAdapter(listAdapter);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadItemLists();

        return binding.getRoot();
    }

    private void loadItemLists() {
        mDatabase.child("lists").child(mAuth.getCurrentUser().getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ItemList list = dataSnapshot.getValue(ItemList.class);
                    list.setName(list.getName().substring(0, 1).toUpperCase() + list.getName().substring(1));
                    itemList.add(list);
                }
                listAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listAdapter.setData(itemList);
    }

    private void saveNewList(String name) {
        // The following code checks is the user already has a list with the name they are wanting to add. If it is not a duplicate name,
        // a list with that name is created and stored in the database.
        mDatabase
                .child("lists")
                .child(mAuth.getCurrentUser().getDisplayName())
                .orderByChild("name")
                .equalTo(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(getContext(), "You already have a list with that name", Toast.LENGTH_SHORT).show();
                } else {
                    ItemList list = new ItemList(mAuth.getCurrentUser().getDisplayName() + "/" + name, name);
                    mDatabase
                            .child("lists")
                            .child(mAuth.getCurrentUser().getDisplayName())
                            .child(name)
                            .setValue(list)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "List created successfully", Toast.LENGTH_SHORT).show();
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