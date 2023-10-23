package com.example.trackerapp.ui.list;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackerapp.R;
import com.example.trackerapp.data.model.ItemList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private List<ItemList> itemList;
    private Context context;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public ListAdapter(Context context, DatabaseReference mDatabase, FirebaseAuth mAuth) {
        this.context = context;
        this.mDatabase = mDatabase;
        this.mAuth = mAuth;
    }

    public void setData(List<ItemList> list) {
        this.itemList = list;
    }

    @NonNull
    @Override
    public ListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ItemList list = itemList.get(position);
        if (list == null) {
            return;
        }

        //holder.listImage.setImageResource()
        holder.listName.setText(list.getName());
        holder.itemListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("listName", list.getName().toLowerCase().trim());
                Navigation.findNavController(view).navigate(R.id.action_listFragment_to_itemsFragment, bundle);
            }
        });
        holder.itemListCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Remove List")
                        .setMessage("Are you sure you want to remove this list? " +
                                "Warning: It will remove all the items in this list as well!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeList(list.getName().toLowerCase().trim());
                                notifyItemRemoved(holder.getLayoutPosition());
                                Toast.makeText(context, "List removed successfully", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                builder.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (itemList != null) {
            return itemList.size();
        }
        return 0;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        private ImageView listImage;
        private TextView listName;

        private CardView itemListCard;

        public ListViewHolder(@NonNull View listView) {
            super(listView);

            listImage = listView.findViewById(R.id.listImage);
            listName = listView.findViewById(R.id.listName);
            itemListCard = listView.findViewById(R.id.itemListCard);
        }
    }

    private void removeList(String name) {
        mDatabase.child("lists")
                .child(mAuth.getCurrentUser().getDisplayName())
                .child(name)
                .removeValue();

        mDatabase.child("items")
                .child(mAuth.getCurrentUser().getDisplayName())
                .child("lists")
                .child(name)
                .removeValue();
    }
}
