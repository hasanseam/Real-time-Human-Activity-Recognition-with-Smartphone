package com.hasanur.realtimehar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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

   private LineChart mChart;
   private Thread thread;
   private boolean plotData = true;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_data_acquisition, container, false);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
       // sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sensorDataTextView = fragmentView.findViewById(R.id.sensor_data_text_view1);
        sensorDataStringBuilder = new StringBuilder();

        dataAcquisitionViewModel = new ViewModelProvider(requireActivity()).get(DataAcquisitionViewModel.class);

        mChart = (LineChart) fragmentView.findViewById(R.id.chartAcc);

        setmChart();

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

    private void setmChart(){
        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);


        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);


        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(-20f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();
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

    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set1 = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(0);
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

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
            data.addEntry(new Entry(set1.getEntryCount(), event.values[1] ), 1);
            data.addEntry(new Entry(set2.getEntryCount(), event.values[2] ), 2);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
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
        dataAcquisitionViewModel.setListening(true);
        startRecordingButton.setText("Stop");


            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor != null) {
                sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
    }

    private void stopListening() {
        dataAcquisitionViewModel.setListening(false);
        startRecordingButton.setText("Start");
        sensorManager.unregisterListener(sensorEventListener);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if(plotData){
                addEntry(event);
                plotData = false;
            }

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