package com.hasanur.realtimehar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.hasanur.realtimehar.databinding.ActivityMainBinding;
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String SELECTED_ITEM_ID = "SELECTED_ITEM_ID";
    private int selectedItem;
    private Fragment dataAcquisitionFragment, configureFragment, profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataAcquisitionFragment = new DataAcquisitionFragment();
        configureFragment = new ConfigureFragment();
        profileFragment = new ProfileFragment();

        // set listener for bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    Fragment replacedFragment;
                    switch (item.getItemId()) {
                        case R.id.data_acquisition:
                            selectedItem = R.id.data_acquisition;
                            break;
                        case R.id.configure:
                            selectedItem = R.id.configure;
                            break;
                        case R.id.profile:
                            selectedItem = R.id.profile;
                            break;
                    }
                    replaceFragment(getSelectedFragment());
                    return true;
                });

        if (savedInstanceState != null) {
            selectedItem = savedInstanceState.getInt(SELECTED_ITEM_ID);
        } else {
            selectedItem = R.id.data_acquisition;
        }
        replaceFragment(getSelectedFragment());
    }

    //onSaveInstanceState function is used to persistent the state after rotation
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_ID, selectedItem);
    }

    //replaceFragment function used to change the fragmentation based on user action
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame_layout_main_activity,fragment);
        fragmentTransaction.commit();
    }

    // getSelectedFragment function used to get the current active fragment which is saved into selectedItem
    private Fragment getSelectedFragment() {
        switch (selectedItem) {
            case R.id.data_acquisition:
                return dataAcquisitionFragment;
            case R.id.configure:
                return configureFragment;
            case R.id.profile:
                return profileFragment;
            default:
                return null;
        }
    }


}
