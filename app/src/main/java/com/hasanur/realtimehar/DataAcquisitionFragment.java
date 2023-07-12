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
import androidx.lifecycle.ViewModelProvider;

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

import com.hasanur.realtimehar.ViewModel.DataAcquisitionViewModel;

import java.util.Arrays;
import java.util.List;

public class DataAcquisitionFragment extends Fragment {
    private SensorManager sensorManager;
    private SharedPreferences sharedPreferences;

    private List<Sensor> sensorList;
    private TextView sensorDataTextView;
    private StringBuilder sensorDataStringBuilder;
    private Button startRecordingButton;
    private DataAcquisitionViewModel dataAcquisitionViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_data_acquisition, container, false);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
       // sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sensorDataTextView = fragmentView.findViewById(R.id.sensor_data_text_view1);
        sensorDataStringBuilder = new StringBuilder();

         dataAcquisitionViewModel = new ViewModelProvider(requireActivity()).get(DataAcquisitionViewModel.class);

        Button selectSensorsButton = fragmentView.findViewById(R.id.select_sensors_button1);

        startRecordingButton = fragmentView.findViewById(R.id.start_recording_button1);

        Log.d("DataACQ", "onCreateView: "+dataAcquisitionViewModel.getListening());
        if(dataAcquisitionViewModel.getListening()){
            startListening();
            startRecordingButton.setText("Stop");
        }else {
            stopListening();
        }

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

    private void startListening() {
        dataAcquisitionViewModel.setListening(true);
        startRecordingButton.setText("Stop");
    }

    private void stopListening() {
        dataAcquisitionViewModel.setListening(false);
        startRecordingButton.setText("Start");
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

    @Override
    public void onResume() {
        super.onResume();
    }
}