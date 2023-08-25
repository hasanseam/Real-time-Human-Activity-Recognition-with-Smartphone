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


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataAcquisitionFragment extends Fragment {
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


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_data_acquisition, container, false);

        requestPermission();
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
       // sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sensorDataTextView = fragmentView.findViewById(R.id.sensor_data_text_view1);
        chartContainer = fragmentView.findViewById(R.id.chartContainer);// for insert the chart dynamically

        sensorDataStringBuilder = new StringBuilder();

        dataAcquisitionViewModel = new ViewModelProvider(requireActivity()).get(DataAcquisitionViewModel.class);
        activityConfigureViewModel = new ViewModelProvider(requireActivity()).get(ActivityConfigureViewModel.class);
        sensorConfigureViewModel = new ViewModelProvider(requireActivity()).get(SensorConfigureViewModel.class);


        feedMultiple();// thread has been used for live input

        startRecordingButton = fragmentView.findViewById(R.id.start_recording_button1);

        Log.d("DataACQ", "onCreateView: "+dataAcquisitionViewModel.getListening());

        if(dataAcquisitionViewModel.getListening()){
            startListening();
            startRecordingButton.setText("Stop");
        }else {
            stopListening();
        }

        getStorageDir();

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
        //checking the sensor configuration
        List<Sensor> checkedSensors = sensorConfigureViewModel.getCheckedSensors();

        List<LineChart> lineCharts = dataAcquisitionViewModel.getChartList();

        chartContainer.removeAllViews();

        for(LineChart chart: lineCharts){
            ViewGroup parentView = (ViewGroup) chart.getParent();
            if (parentView != null) {
                parentView.removeView(chart);
            }
            chartContainer.addView(chart);
        }

        Log.d("ViewModelCheck",""+lineCharts.size());

        for(Sensor sensor:checkedSensors){

            if(checkedSensors.size()>lineCharts.size()){
                createChart(sensor.getName());
            }
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

        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writer = null;
        }
    }
    private SensorEventListener createSensorEventListener() {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                plotDataForCharts(event,event.sensor);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Handle accuracy changes here
            }
        };
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





    private String getStorageDir() {
        File dir = new File(getActivity().getExternalFilesDir(null), "RealtimeHarData");
        if (!dir.exists()) {
            boolean dirCreated = dir.mkdir();
            Log.d("DirectoryCreated", "Directory created: " + dirCreated);
        }
        return dir.getAbsolutePath();
    }

    private void writeCsvFile(String data) {
        try {
            if (writer == null) {
                Log.d("OW",getStorageDir());
                writer = new FileWriter(new File(getStorageDir(), "sensor_data_" + System.currentTimeMillis() + ".csv"));
            }
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
}