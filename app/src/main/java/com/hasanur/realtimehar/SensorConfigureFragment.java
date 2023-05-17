package com.hasanur.realtimehar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class SensorConfigureFragment extends Fragment {
    private LinearLayout sensorListLayout;
    private ScrollView scrollView;
    private List<String> selectedSensors = new ArrayList<>();
    private OnSensorSelectionListener sensorSelectionListener;

    private SensorManager sensorManager;

    // Interface for sensor selection callback
    public interface OnSensorSelectionListener {
        void onSensorSelected(String sensor);
        void onSensorDeselected(String sensor);
    }

    public void setOnSensorSelectionListener(OnSensorSelectionListener listener) {
        this.sensorSelectionListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_sensor, container, false);

        scrollView = fragmentView.findViewById(R.id.scroll_view);
        sensorListLayout = fragmentView.findViewById(R.id.sensor_list_layout);

        // Populate the sensor list dynamically
        populateSensorList();

        return fragmentView;
    }

    private void populateSensorList() {
        // Get the list of available sensors
        List<String> availableSensors = getAvailableSensors();

        // Create and add CheckBox views for each sensor
        for (String sensor : availableSensors) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(sensor);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Add the selected sensor to the list
                        selectedSensors.add(sensor);
                        if (sensorSelectionListener != null) {
                            sensorSelectionListener.onSensorSelected(sensor);
                        }
                    } else {
                        // Remove the deselected sensor from the list
                        selectedSensors.remove(sensor);
                        if (sensorSelectionListener != null) {
                            sensorSelectionListener.onSensorDeselected(sensor);
                        }
                    }
                }
            });

            sensorListLayout.addView(checkBox);
        }
    }

    private List<String> getAvailableSensors() {
        // TODO: Implement your logic to retrieve the available sensors
        // Return a list of available sensor names
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        // Get the list of all sensors
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sensors = new ArrayList<>();
        int i =0;
        for (Sensor sensor : sensorList) {
            sensors.add(sensor.getName()+(i++));
        }
        return sensors;
    }
}