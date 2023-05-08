package com.hasanur.realtimehar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class DataAcquisitionFragment extends Fragment {
    private SensorManager sensorManager;
    private SharedPreferences sharedPreferences;

    private List<Sensor> sensorList;
    private TextView sensorDataTextView;
    private StringBuilder sensorDataStringBuilder;
    private Button startRecordingButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_data_acquisition, container, false);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sensorDataTextView = fragmentView.findViewById(R.id.sensor_data_text_view1);
        sensorDataStringBuilder = new StringBuilder();

        Button selectSensorsButton = fragmentView.findViewById(R.id.select_sensors_button1);

        selectSensorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSensorCheckModal();
            }
        });

        startRecordingButton = fragmentView.findViewById(R.id.start_recording_button1);
        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startRecordingButton.getText().toString().equals("Stop")){
                    stopListening();
                }
                else{
                    startListening();
                }
            }
        });
        // Inflate the layout for this fragment
        return fragmentView;
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Get the sensor data
            float[] values = event.values;
            int sensorType = event.sensor.getType();

            long timestamp = System.currentTimeMillis();

            // Determine the sensor type and add an annotation to the data string
            String sensorTypeString;
            switch (sensorType) {
                case Sensor.TYPE_ACCELEROMETER:
                    sensorTypeString = "ACCELEROMETER";
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    sensorTypeString = "GYROSCOPE";
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sensorTypeString = "MAGNETOMETER";
                    break;
                default:
                    sensorTypeString = "UNKNOWN";
                    break;
            }

            // Convert the data to a string with the sensor type annotation
            String sensorDataString = timestamp+" "+sensorTypeString + ": " + Arrays.toString(values);
            Log.d("SENSOR_DATA", sensorDataString);

            sensorDataTextView.setText(sensorDataString);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes here
        }
    };

    private void stopListening(){
        sensorManager.unregisterListener(sensorEventListener);
        startRecordingButton.setText("Start");
    }

    private void startListening(){
        {
            startRecordingButton.setText("Stop");
            // Get the selected sensors
            String selectedSensors = sharedPreferences.getString("MyPrefs", "");
            String[] selectedSensorArray = selectedSensors.split(",");

            // Register sensor listeners for the selected sensors
            for (String sensorName : selectedSensorArray) {
                Log.d("HELL", sensorName);
                Sensor sensor = sensorManager.getDefaultSensor(getSensorType(sensorName));
                if (sensor != null) {
                    sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            // Start recording
            boolean isRecording = true;
            Toast.makeText(requireContext(), "Recording started", Toast.LENGTH_SHORT).show();
        }
    }

    private int getSensorType(String sensorName) {
        switch (sensorName) {
            case "Accelerometer":
                return Sensor.TYPE_ACCELEROMETER;
            case "Magnetometer":
                return Sensor.TYPE_MAGNETIC_FIELD;
            case "Gyroscope":
                return Sensor.TYPE_GYROSCOPE;
            default:
                return -1;
        }
    }

    private void openSensorCheckModal() {
        // Create a list of sensors
        String[] sensors = {"Accelerometer", "Magnetometer", "Gyroscope"};

        // Create an ArrayAdapter to populate the ListView with the sensors
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, sensors);

        // Create the ListView
        ListView listView = new ListView(requireContext());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        // Check the previously selected sensors
        String selectedSensors = sharedPreferences.getString("MyPrefs", "");
        String[] selectedSensorArray = selectedSensors.split(",");
        for (int i = 0; i < selectedSensorArray.length; i++) {
            int position = Arrays.asList(sensors).indexOf(selectedSensorArray[i]);
            if (position != -1) {
                listView.setItemChecked(position, true);
            }
        }

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Sensors");
        builder.setView(listView);
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SparseBooleanArray selectedSensors = listView.getCheckedItemPositions();

                // Save the selected sensors to SharedPreferences
                StringBuilder selectedSensorNames = new StringBuilder();
                for (int i = 0; i < selectedSensors.size(); i++) {
                    int position = selectedSensors.keyAt(i);
                    if (selectedSensors.get(position)) {
                        selectedSensorNames.append(sensors[position]).append(",");
                    }
                }
                String selectedSensorNamesString = selectedSensorNames.toString();
                if (!selectedSensorNamesString.isEmpty()) {
                    selectedSensorNamesString = selectedSensorNamesString.substring(0, selectedSensorNamesString.length() - 1);
                }
                sharedPreferences.edit().putString("MyPrefs", selectedSensorNamesString).apply();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}