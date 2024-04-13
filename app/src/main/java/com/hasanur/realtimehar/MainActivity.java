package com.hasanur.realtimehar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hasanur.realtimehar.ViewModel.DataAcquisitionViewModel;
import com.hasanur.realtimehar.ViewModel.DataVisualizationViewModel;
import com.hasanur.realtimehar.databinding.ActivityMainBinding;
import com.hasanur.realtimehar.services.KeepAliveService;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String SELECTED_ITEM_ID = "SELECTED_ITEM_ID";
    private int selectedItem;
    private Fragment dataAcquisitionFragment, configureFragment, profileFragment;

    private DataAcquisitionViewModel dataAcquisitionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dataAcquisitionFragment = new DataAcquisitionFragment();
        configureFragment = new ConfigureFragment();
        profileFragment = new ProfileFragment();

        dataAcquisitionViewModel = new ViewModelProvider(this).get(DataAcquisitionViewModel.class);

        // set listener for bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    if(!dataAcquisitionViewModel.getListening()){
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
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Navigation disabled while listening is active.");
                        builder.setPositiveButton("OK", null);
                        builder.show();
                    }
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

        if(!fragment.isAdded()){
            fragmentTransaction.replace(R.id.frame_layout_main_activity, fragment);
            fragmentTransaction.commit();
        }
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
