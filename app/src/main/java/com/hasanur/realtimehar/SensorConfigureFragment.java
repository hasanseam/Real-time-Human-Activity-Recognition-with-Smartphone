package com.hasanur.realtimehar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.hasanur.realtimehar.ViewModel.DataAcquisitionViewModel;
import com.hasanur.realtimehar.ViewModel.SensorConfigureViewModel;

import java.util.ArrayList;
import java.util.List;

public class SensorConfigureFragment extends Fragment {
    private LinearLayout sensorListLayout;
    private ScrollView scrollView;
    private List<String> selectedSensors = new ArrayList<>();
    private SensorManager sensorManager;

    private SensorConfigureViewModel sensorConfigureViewModel;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_sensor, container, false);

        scrollView = fragmentView.findViewById(R.id.scroll_view);
        sensorListLayout = fragmentView.findViewById(R.id.sensor_list_layout);

        sensorConfigureViewModel = new ViewModelProvider(requireActivity()).get(SensorConfigureViewModel.class);

        // Populate the sensor list dynamically
        populateSensorList();

        return fragmentView;
    }

    private boolean isChecked(Sensor sensor){

        List<Sensor> checkedSensors = sensorConfigureViewModel.getCheckedSensors();

        for(Sensor cSensor :checkedSensors){
            if(cSensor.getName()==sensor.getName()){
                return true;
            }
        }

        return false;
    }

    private void populateSensorList() {
        // Get the list of available sensors
        List<Sensor> availableSensors = getAvailableSensors();


        // Create and add CheckBox views for each sensor
        for (Sensor sensor : availableSensors) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(sensor.getName());
            checkBox.setChecked(isChecked(sensor));
          //  checkBox.setChecked(checkedSensors.contains(sensor));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Add the selected sensor to the
                        sensorConfigureViewModel.addCheckedSensors(sensor);
                       // selectedSensors.add(sensor);
                    } else {
                        // Remove the deselected sensor from the list
                        sensorConfigureViewModel.removeCheckedSensors(sensor);
                       // selectedSensors.remove(sensor);
                    }
                }
            });

            sensorListLayout.addView(checkBox);
        }
    }

    private List<Sensor> getAvailableSensors() {
        // TODO: Implement your logic to retrieve the available sensors
        // Return a list of available sensor names
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        // Get the list of all sensors
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        return sensors;
    }
}