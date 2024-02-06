package com.hasanur.realtimehar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
    private SharedPreferences sharedPreferences;
    private List<Sensor> sensorList;
    private TextView sensorDataTextView;
    private StringBuilder sensorDataStringBuilder;
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
   private FileWriter writer;

    private FileWriter csvWriter;
    private File csvFile;
    private static final String FILE_NAME = "sensor_data1.csv";
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
        sensorDataTextView = fragmentView.findViewById(R.id.sensor_data_text_view1);
        chartContainer = fragmentView.findViewById(R.id.chartContainer);// for insert the chart dynamically

        sensorDataStringBuilder = new StringBuilder();

        dataAcquisitionViewModel = new ViewModelProvider(requireActivity()).get(DataAcquisitionViewModel.class);
        activityConfigureViewModel = new ViewModelProvider(requireActivity()).get(ActivityConfigureViewModel.class);
        sensorConfigureViewModel = new ViewModelProvider(requireActivity()).get(SensorConfigureViewModel.class);

        // thread has been used for live input
        feedMultiple();

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

        initFileWriter();

        isHeaderWritten = false;

        //checking the sensor configuration
        List<Sensor> checkedSensors = sensorConfigureViewModel.getCheckedSensors();

       // List<LineChart> lineCharts = dataAcquisitionViewModel.getChartList();

        chartContainer.removeAllViews();

        //wrong logic To Do

       /* for(LineChart chart: lineCharts){
            ViewGroup parentView = (ViewGroup) chart.getParent();
            if (parentView != null) {
                parentView.removeView(chart);
            }
            chartContainer.addView(chart);
        }*/

        //Log.d("ViewModelCheck",""+lineCharts.size());

        for(Sensor sensor:checkedSensors){

           /* if(checkedSensors.size()>lineCharts.size()){
                createChart(sensor.getName());
            }*/
            createChart(sensor.getName());
            SensorEventListener sensorEventListener1 = createSensorEventListener();
            sensorEventListeners.add(sensorEventListener1);
            sensorManager.registerListener(sensorEventListener1, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        Sensor sensor = (checkedSensors.size()>0) ? checkedSensors.get(0):null;
        if (sensor != null) {
            dataAcquisitionViewModel.setListening(true); //view model update as it register the listener
            startRecordingButton.setText("Stop");
        }else{
            Toast.makeText(getActivity(),"Configure your sensors",Toast.LENGTH_SHORT).show();
        }
    }

    private void stopListening()  {
        dataAcquisitionViewModel.setListening(false);
        startRecordingButton.setText("Start");
        // unregister all listener
        for(SensorEventListener sensorEventListener1:sensorEventListeners){
            sensorManager.unregisterListener(sensorEventListener1);
        }
        chartContainer.removeAllViews();
       // chartContainer.postInvalidate();
        dataAcquisitionViewModel.resetChartList();
        closeFileWriter();
    }
    private SensorEventListener createSensorEventListener() {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                latestSensorReadings.put(event.sensor, event.values);
                boolean allDataAvailable = true;
                for (Sensor sensor : sensorConfigureViewModel.getCheckedSensors()) {
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
                for (Sensor sensor : sensorConfigureViewModel.getCheckedSensors()) {
                    String[] channels = {"_x", "_y", "_z","_a","_b"}; // Define the channels

                    float[] values = latestSensorReadings.get(sensor);

                    for (int i = 0; i < values.length && i < channels.length; i++) {
                        sensorDataString.append(sensor.getName()).append(channels[i]).append(",");
                    }

                }

                // Append selected activities header
                sensorDataString.append("Activities\n");

                // Write the sensor data string to the CSV file
                csvWriter.write(sensorDataString.toString());

                // Append data from all registered sensors
                sensorDataString.setLength(0);
                isHeaderWritten = true;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            sensorDataString.append(timestamp).append(",");

            // Append data from all registered sensors
            for (Sensor sensor : sensorConfigureViewModel.getCheckedSensors()) {
                float[] values = latestSensorReadings.get(sensor);
                for (float value : values) {
                    sensorDataString.append(value).append(",");
                }
            }

            // Append selected activities
            sensorDataString.append(activityConfigureViewModel.getSelectedActivities()).append("\n");

            // Write the sensor data string to the CSV file
            csvWriter.write(sensorDataString.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void plotDataForCharts(SensorEvent event,Sensor sensor) {
        List<Sensor> checkedSensors = sensorConfigureViewModel.getCheckedSensors();
        int sensorIndex = checkedSensors.indexOf(sensor);
        if (sensorIndex >= 0) {
            addEntry2(event, sensorIndex);
        }
    }

    private int getColorForSensor(int sensorIndex) {
        int[] colors = {Color.MAGENTA, Color.GREEN, Color.RED};
        return colors[sensorIndex % colors.length];
    }

    private void addEntry2(SensorEvent event, int sensorIndex) {
        List<LineChart> chartList = dataAcquisitionViewModel.getChartList();
        LineData data = chartList.get(sensorIndex).getData();
        LineChart chart = chartList.get(sensorIndex);
        Log.d("Hello","in add entry "+data.toString());
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
        String fileName = "activitydata" + timestamp + ".csv";

        csvFile = new File(dir, fileName);

        try {
            // FileWriter in append mode to keep adding data
            csvWriter = new FileWriter(csvFile, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void writeSensorDataToFile(SensorEvent event) {
        try {
            StringBuilder sensorDataString = new StringBuilder();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            sensorDataString.append(timestamp).append(",");
            for (float value : event.values) {
                sensorDataString.append(value).append(",");
            }
            sensorDataString.append(event.sensor.getName()).append(",");
            sensorDataString.append(activityConfigureViewModel.getSelectedActivities());
            sensorDataString.append("\n");
            csvWriter.write(sensorDataString.toString());
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

    private void readSpecificCSVFileFromExternalStorage(String fileName) {
        // Get the external storage directory for the "RealtimeHAR" folder
        File directory = new File(requireContext().getExternalFilesDir(null), "RealtimeHAR");

        // Construct the file path
        File fileToRead = new File(directory, fileName);

        if (fileToRead.exists() && fileToRead.isFile() && fileToRead.getName().endsWith(".csv")) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    // Process each line (for example, print it)
                    System.out.println(line);
                    Log.d("Filedata",line);
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File not found or not a CSV file");
        }
    }

}