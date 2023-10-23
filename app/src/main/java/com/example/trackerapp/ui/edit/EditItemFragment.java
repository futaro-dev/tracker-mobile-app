package com.example.trackerapp.ui.edit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import com.example.trackerapp.databinding.FragmentEditItemBinding;
import com.example.trackerapp.ui.item.ImageController;
import com.example.trackerapp.ui.item.ItemController;

public class EditItemFragment extends Fragment {
    private FragmentEditItemBinding binding;

    private String listName, itemName;
    private String status, progress;
    private int score;

    private ItemController itemController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditItemBinding.inflate(inflater, container, false);

        String[] parts = getArguments().getString("itemId").split("/");
        listName = parts[1];
        itemName = parts[2];

        itemController = new ItemController(getContext(), listName, itemName);

        status = getArguments().getString("status");
        score = Integer.parseInt(getArguments().getString("score"));
        progress = getArguments().getString("progress");

        Spinner itemStatus = binding.editItemStatus;
        EditText itemProgress = binding.editItemProgress;
        Spinner itemScore = binding.editItemScore;

        itemProgress.setText(progress);

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

        binding.updateItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemProgress.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Progress cannot be blank", Toast.LENGTH_SHORT).show();
                } else {
                    itemController.updateItem(status, Integer.parseInt(itemProgress.getText().toString().trim()), score);
                    Navigation.findNavController(view).navigate(R.id.action_editItemFragment_to_listFragment);
                }
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        return binding.getRoot();
    }
}