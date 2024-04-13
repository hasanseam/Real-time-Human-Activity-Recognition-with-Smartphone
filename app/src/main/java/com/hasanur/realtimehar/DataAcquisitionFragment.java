package com.hasanur.realtimehar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.hasanur.realtimehar.ViewModel.ActivityConfigureViewModel;
import com.hasanur.realtimehar.ViewModel.DataAcquisitionViewModel;
import com.hasanur.realtimehar.ViewModel.SensorConfigureViewModel;
import com.hasanur.realtimehar.services.KeepAliveService;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataAcquisitionFragment extends Fragment {

    private Map<Sensor, float[]> latestSensorReadings = new HashMap<>();
    private boolean isHeaderWritten;
    private SensorManager sensorManager;
    private Button startRecordingButton;
    private LinearLayout chartContainer;
    private List<LineChart> chartList = new ArrayList<>();
    private List<ILineDataSet> sensorDataSets = new ArrayList<>();
    private List <SensorEventListener> sensorEventListeners = new ArrayList<>();
    private DataAcquisitionViewModel dataAcquisitionViewModel;
    private ActivityConfigureViewModel activityConfigureViewModel;
    private SensorConfigureViewModel sensorConfigureViewModel;
   private Thread thread;
   private boolean plotData = true;
   private static final int PERMISSION_REQUEST_CODE = 1;

    private FileWriter csvWriter;
    private File csvFile;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_data_acquisition, container, false);

        // request permission for write
        requestPermission();

        //initialization and view setup
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        chartContainer = fragmentView.findViewById(R.id.chartContainer);// for insert the chart dynamically


        dataAcquisitionViewModel = new ViewModelProvider(requireActivity()).get(DataAcquisitionViewModel.class);
        activityConfigureViewModel = new ViewModelProvider(requireActivity()).get(ActivityConfigureViewModel.class);
        sensorConfigureViewModel = new ViewModelProvider(requireActivity()).get(SensorConfigureViewModel.class);

        // thread has been used for live input
        feedMultiple();

        startRecordingButton = fragmentView.findViewById(R.id.start_recording_button1);
        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startRecordingButton.getText().toString().equals("Stop")){
                    stopListening();
                    stopKeepAliveService();
                }
                else{

                    startListening();
                }
            }
        });
        // Inflate the layout for this fragment
        return fragmentView;
    }

    private void startKeepAliveService() {
        // Start the KeepAliveService
        requireActivity().startService(new Intent(requireActivity(), KeepAliveService.class));
    }

    private void stopKeepAliveService() {
        // Stop the KeepAliveService
        requireActivity().stopService(new Intent(requireActivity(), KeepAliveService.class));
    }

    private void createChart(String chartTitle) {
        LineChart lineChart = configureChart();
        Description description = new Description();
        description.setText(chartTitle);
        lineChart.setDescription(description);
        chartContainer.addView(lineChart);
        dataAcquisitionViewModel.setChartList(lineChart);
    }

    private LineChart configureChart(){

        LineChart lineChart = new LineChart(getActivity());
        lineChart.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500
        ));

        // enable description text
        lineChart.getDescription().setEnabled(true);

        // enable touch gestures
        lineChart.setTouchEnabled(true);
        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);


        // set an alternative background color
        lineChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        lineChart.setData(data);


        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(-20f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setDrawBorders(false);

        return lineChart;

    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }




    private LineDataSet createSet(int color) {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(color);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void startListening() {

        List<Sensor> checkedSensors = sensorConfigureViewModel.getCheckedSensors();
        Sensor s = (checkedSensors.size()>0) ? checkedSensors.get(0):null;
        if (s != null && activityConfigureViewModel.getSelectedActivities()!=null) {
            //start foreground service to keep alive the app for getting the sensor data even after switch off
            startKeepAliveService();
            // file initialize
            initFileWriter();

            isHeaderWritten = false;

            chartContainer.removeAllViews();

            for(Sensor sensor:checkedSensors){
                createChart(sensor.getName());
                SensorEventListener sensorEventListener1 = createSensorEventListener();

                // Calculate the delay in microseconds for the desired frequency (100Hz)
                int samplingPeriodUs = (1000000*60)/ 900; // 100 Hz = 100 samples per second = 1000000 microseconds

                boolean isRegistered = sensorManager.registerListener(sensorEventListener1, sensor,SensorManager.SENSOR_DELAY_NORMAL);
                if(isRegistered){
                    sensorEventListeners.add(sensorEventListener1);
                    sensorConfigureViewModel.addRegisteredSensors(sensor);
                }
                else{

                }
            }
            dataAcquisitionViewModel.setListening(true); //view model update as it register the listener
            startRecordingButton.setText("Stop");
        }else{
            Toast.makeText(getActivity(),"Configure your sensors and Activity",Toast.LENGTH_SHORT).show();
        }
    }

    private void stopListening()  {
        dataAcquisitionViewModel.setListening(false);
        startRecordingButton.setText("Start");
        // unregister all listener
        for(SensorEventListener sensorEventListener1:sensorEventListeners){
            sensorManager.unregisterListener(sensorEventListener1);
        }
        //remove all views
        chartContainer.removeAllViews();
        dataAcquisitionViewModel.resetChartList();
        sensorConfigureViewModel.removeRegistredSensors();
        //close all files
        closeFileWriter();
    }
    private SensorEventListener createSensorEventListener() {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                latestSensorReadings.put(event.sensor, event.values);
                boolean allDataAvailable = true;
                for (Sensor sensor : sensorConfigureViewModel.getRegisteredSensors()) {
                    if (!latestSensorReadings.containsKey(sensor)) {
                        allDataAvailable = false;
                        break;
                    }
                }
                // If data is available for all sensors, write to the file
                if (allDataAvailable) {
                    writeSensorDataToFile();
                }
                //old code
               plotDataForCharts(event,event.sensor);
               // writeSensorDataToFile(event);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Handle accuracy changes here
            }
        };
    }

    private void writeSensorDataToFile() {
        try {
            StringBuilder sensorDataString = new StringBuilder();
            if(!isHeaderWritten){
                sensorDataString.append("Timestamp,");

                // Append headers for each registered sensor
                for (Sensor sensor : sensorConfigureViewModel.getRegisteredSensors()) {
                    String[] channels = {" x", " y", " z"," a"," b"," c"," d"," e"," f"}; // Define the channels

                    float[] values = latestSensorReadings.get(sensor);

                    for (int i = 0; i < values.length && i < channels.length; i++) {
                        sensorDataString.append(sensor.getName().toUpperCase()).append(channels[i]).append(",");
                    }

                }
                sensorDataString.setLength(sensorDataString.length() - 1);
                sensorDataString.append("\n");
                // Write the sensor data string to the CSV file
                csvWriter.write(sensorDataString.toString());

                // Append data from all registered sensors
                sensorDataString.setLength(0);
                isHeaderWritten = true;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            sensorDataString.append(timestamp).append(",");

            // Append data from all registered sensors
            for (Sensor sensor : sensorConfigureViewModel.getRegisteredSensors()) {
                float[] values = latestSensorReadings.get(sensor);
                for (float value : values) {
                    sensorDataString.append(value).append(",");
                }
            }
            sensorDataString.setCharAt(sensorDataString.length()-1,' ');
            sensorDataString.append("\n");
            // Write the sensor data string to the CSV file
            csvWriter.write(sensorDataString.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void plotDataForCharts(SensorEvent event,Sensor sensor) {
        List<Sensor> checkedSensors = sensorConfigureViewModel.getRegisteredSensors();
        int sensorIndex = checkedSensors.indexOf(sensor);
        if (sensorIndex >= 0) {
            addEntry(event, sensorIndex);
        }
    }

    private void addEntry(SensorEvent event, int sensorIndex) {
        List<LineChart> chartList = dataAcquisitionViewModel.getChartList();
        LineData data = chartList.get(sensorIndex).getData();
        LineChart chart = chartList.get(sensorIndex);
        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set1 = data.getDataSetByIndex(1);
            ILineDataSet set2 = data.getDataSetByIndex(2);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet(Color.MAGENTA);
                data.addDataSet(set);
            }
            if (set1 == null) {
                set1 = createSet(Color.GREEN);
                data.addDataSet(set1);
            }
            if (set2 == null) {
                set2 = createSet(Color.RED);
                data.addDataSet(set2);
            }

            for(int i = 0; i<event.values.length;i++){
                data.addEntry(new Entry(set.getEntryCount(), event.values[i] ), i);
            }

            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

        }
    }

    // Method called when the fragment is resumed
    @Override
    public void onResume() {
        super.onResume();
        //other task in Resume()
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    // Request permission for writing to external storage
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    // Initialize the FileWriter and create the file for writing
    private void initFileWriter() {

        File dir = new File(requireContext().getExternalFilesDir(null), "RealtimeHAR");

        // Make sure the directory exists, if not, create it
        if (!dir.exists()) {
            dir.mkdirs(); // This will create the directory if it doesn't exist
        }

        //customized file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // Construct the file name with the timestamp
        String fileName = activityConfigureViewModel.getSelectedActivities()+timestamp + ".csv";

        csvFile = new File(dir, fileName);

        try {
            // FileWriter in append mode to keep adding data
            csvWriter = new FileWriter(csvFile, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //close the file writer
    //the function has been help to save csv
    private void closeFileWriter() {

        try {
            if (csvWriter != null) {
                csvWriter.close();
                Toast.makeText(getActivity(), "Sensor data saved to " + csvFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                Log.d("path",csvFile.getAbsolutePath());
               // readSpecificCSVFileFromExternalStorage(csvFile.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}