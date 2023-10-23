package com.example.trackerapp.ui.items;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackerapp.R;
import com.example.trackerapp.data.model.Item;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {
    private List<Item> items;

    public void setData(List<Item> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemsAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ItemViewHolder holder, int position) {
        Item item = items.get(position);
        if (item == null) {
            return;
        }

        // holder.itemImage.setImageResource();
        holder.itemName.setText(item.getName());
        holder.itemScore.setText(Integer.toString(item.getScore()));
        holder.itemProgress.setText(Integer.toString(item.getProgress()));
        holder.itemStatus.setText(item.getStatus());
        holder.itemItemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("itemId", item.getId());
                Navigation.findNavController(view).navigate(R.id.action_itemsFragment_to_itemFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemName, itemScore, itemProgress, itemStatus;

        private CardView itemItemCard;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemScore = itemView.findViewById(R.id.itemScore);
            itemProgress = itemView.findViewById(R.id.itemProgress);
            itemStatus = itemView.findViewById(R.id.itemStatus);
            itemItemCard = itemView.findViewById(R.id.itemItemCard);
        }
    }
}
