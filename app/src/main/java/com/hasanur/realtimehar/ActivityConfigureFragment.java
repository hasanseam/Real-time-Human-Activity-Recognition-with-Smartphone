package com.hasanur.realtimehar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;


public class ActivityConfigureFragment extends Fragment {

    private ChipGroup chipGroup;
    private EditText chipEditText;
    private Button addChipButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_activity, container, false);
        // Initialize views
        chipGroup = fragmentView.findViewById(R.id.chip_group);
        chipEditText = fragmentView.findViewById(R.id.chip_edit_text);
        addChipButton = fragmentView.findViewById(R.id.add_chip_button);

        // Set click listener for the add chip button
        addChipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChip();
            }
        });

        // Inflate the layout for this fragment
        return fragmentView;
    }

    private void addChip() {
        // Get the text from the EditText
        String text = chipEditText.getText().toString().trim();

        // Check if text is empty
        if (!text.isEmpty()) {

            // Create a new chip
            Chip chip = new Chip(getContext());
            chip.setText(text);
            chip.setCloseIconVisible(true);
            chip.setCheckable(true);
            //chip.setChipIconVisible(true);


            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < chipGroup.getChildCount(); i++) {
                        Chip childChip = (Chip) chipGroup.getChildAt(i);
                        childChip.setSelected(false);
                    }
                    Log.d("BALSAL", "Bal amar");
                    chip.setSelected(!chip.isSelected());

                }
            });

            // Set click listener for the close icon of the chip
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

