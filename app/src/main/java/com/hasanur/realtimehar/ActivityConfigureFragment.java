package com.hasanur.realtimehar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.hasanur.realtimehar.ViewModel.ActivityConfigureViewModel;
import com.hasanur.realtimehar.ViewModel.DataAcquisitionViewModel;

import java.util.ArrayList;


public class ActivityConfigureFragment extends Fragment {

    private ChipGroup chipGroup;
    private EditText chipEditText;
    private Button addChipButton;

    private ActivityConfigureViewModel activityConfigureViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_activity, container, false);
        // Initialize views
        chipGroup = fragmentView.findViewById(R.id.chip_group);
        chipEditText = fragmentView.findViewById(R.id.chip_edit_text);
        addChipButton = fragmentView.findViewById(R.id.add_chip_button);

        activityConfigureViewModel = new ViewModelProvider(requireActivity()).get(ActivityConfigureViewModel.class);

        // Set click listener for the add chip button
        addChipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the EditText
                String text = chipEditText.getText().toString().trim();
                activityConfigureViewModel.addAcitivity(text);
                addChip(text,false);
            }
        });
        ArrayList<String> activities = activityConfigureViewModel.getActivities();
        for(int i = 0; i<activityConfigureViewModel.getActivities().size(); i++){
              addChip(activities.get(i),i==activityConfigureViewModel.getSelectedPosition());
        }

        // Inflate the layout for this fragment
        return fragmentView;
    }

    private void addChip(String text, boolean isSelected ) {

        // Check if text is empty
        if (!text.isEmpty()) {

            // Create a new chip
            Chip chip = new Chip(getContext());
            chip.setText(text);
            chip.setCloseIconVisible(true);
            chip.setCheckable(true);
            //chip.setChipIconVisible(true);
            chip.setChecked(isSelected);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position;
                    if(chip.isChecked() ){
                       position =  chipGroup.indexOfChild(v);
                    }else{
                        position = -1;
                    }
                    activityConfigureViewModel.setSelectedPosition(position);
                }
            });
            // Set click listener for the close icon of the chip
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = chipGroup.indexOfChild(v);
                    activityConfigureViewModel.removeActivity(position);
                    chipGroup.removeView(chip);
                }
            });

            // Add the chip to the chip group
            chipGroup.addView(chip);

            // Clear the text from the EditText
            chipEditText.getText().clear();
        }
    }




}

