package com.hasanur.realtimehar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private SharedPreferences sharedPreferences;
    private TextView sensorDataTextView;
    private StringBuilder sensorDataStringBuilder;
    private  Button startRecordingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        sensorDataTextView = findViewById(R.id.sensor_data_text_view);
        sensorDataStringBuilder = new StringBuilder();

        Button selectSensorsButton = findViewById(R.id.select_sensors_button);
        selectSensorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSensorCheckModal();
            }
        });
        Spinner activitySpinner = findViewById(R.id.activity_spinner);
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedActivity = parent.getItemAtPosition(position).toString();
                // Do something with the selected activity
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        startRecordingButton = findViewById(R.id.start_recording_button);
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

    }

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
            Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_SHORT).show();
        }
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


    private void openSensorCheckModal() {
        // Create a list of sensors
        String[] sensors = {"Accelerometer", "Magnetometer", "Gyroscope"};

        // Create an ArrayAdapter to populate the ListView with the sensors
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, sensors);

        // Create the ListView
        ListView listView = new ListView(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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


}
