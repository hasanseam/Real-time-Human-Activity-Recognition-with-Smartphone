package com.hasanur.realtimehar;

import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.hasanur.realtimehar.DatabaseHelper.SensorDbHelper;
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

    private SensorDbHelper sensorDbHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_sensor, container, false);

        scrollView = fragmentView.findViewById(R.id.scroll_view);
        sensorListLayout = fragmentView.findViewById(R.id.sensor_list_layout);

        sensorConfigureViewModel = new ViewModelProvider(requireActivity()).get(SensorConfigureViewModel.class);

        sensorDbHelper = new SensorDbHelper(requireContext());
        // Populate the sensor list dynamically
        List <String> sensorListFromDB = getCheckedSensorsFromDB();
        if(sensorListFromDB.size()!=0){
            loadCheckedSensors(sensorListFromDB);
        }
        populateSensorList();
        return fragmentView;
    }

    private void loadCheckedSensors(List<String> sensorListFromDB){
        List<Sensor> availableSensors = getAvailableSensors();

        for(Sensor sensor:availableSensors){
            if(sensorListFromDB.contains(sensor.getName())){
                sensorConfigureViewModel.addCheckedSensors(sensor);
            }
        }
    }

    private List<String> getCheckedSensorsFromDB(){
        List <String> sensorListFromDB = new ArrayList<String>();
        Cursor cursor = sensorDbHelper.readAllData();
        if(cursor.getCount()==0){
            Log.d("Cursor","DATA NAI");

        }else{
            while(cursor.moveToNext()){
                sensorListFromDB.add(cursor.getString(1));
            }
        }
        return sensorListFromDB;
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
                        sensorDbHelper.addSensorName(sensor.getName());

                    } else {
                        // Remove the deselected sensor from the list
                        sensorConfigureViewModel.removeCheckedSensors(sensor);
                        sensorDbHelper.deleteSensorName(sensor.getName());
                    }
                }
            });
            sensorListLayout.addView(checkBox);
        }
    }

    private List<Sensor> getAvailableSensors() {
        // Return a list of available sensor names
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        // Get the list of all sensors
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        return sensors;
    }
}