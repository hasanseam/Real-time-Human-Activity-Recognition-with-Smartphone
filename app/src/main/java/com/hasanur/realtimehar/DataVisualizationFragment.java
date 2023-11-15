package com.hasanur.realtimehar;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.hasanur.realtimehar.Model.SensorData;
import com.hasanur.realtimehar.ViewModel.DataVisualizationViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DataVisualizationFragment extends Fragment {

    private DataVisualizationViewModel dataVisualizationViewModel;
    private List <SensorData> sensorDatalist;
    private Hashtable<String, Integer> hashtable;
    private View view;
    String fileName;
    private LineChart mChart;
    private Thread thread;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_data_visualization, container, false);
        dataVisualizationViewModel = new ViewModelProvider(requireActivity()).get(DataVisualizationViewModel.class);
        // Access the views in your layout and populate with file data
        TextView fileNameTextView = view.findViewById(R.id.name);

        sensorDatalist = new ArrayList<>();
        hashtable = new Hashtable<>();

        fileName = dataVisualizationViewModel.getFilename();

        readDataFromFile(fileName);
        fileNameTextView.setText(fileName);

        return view;
    }

    private void readDataFromFile(String fileName) {

        File directory = new File(requireContext().getExternalFilesDir(null), "RealtimeHAR");
        File fileToRead = new File(directory, fileName);

        if (fileToRead.exists() && fileToRead.isFile() && fileToRead.getName().endsWith(".csv")) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    String[] values = line.split(",");
                    long timestampInMillis = convertTimestampToMillis(values[0]);
                    String sensorName = values[values.length-2].toString();
                    if(!hashtable.containsKey(sensorName)){
                        hashtable.put(sensorName,hashtable.size());
                        sensorDatalist.add(new SensorData(sensorName));
                    }

                    if(hashtable.containsKey(sensorName)){
                        int pos = hashtable.get(sensorName);
                        SensorData sensorData = sensorDatalist.get(pos);
                        double [] dataArr  = new double[values.length-3];
                        for(int i = 0; i<values.length-3; i++){
                            dataArr[i] = Double.parseDouble(values[i+1]);
                        }
                        sensorData.addData(dataArr);
                    }
                }
                bufferedReader.close();
                //Log.d("HashTable",hashtable+"");
              //  Log.d("Sensor List",sensorDatalist+"");
               // Log.d("Sensor data", sensorDatalist.get(0).getDataList()+"");
               // Log.d("Sensor data", sensorDatalist.get(0).getDataList().size()+"");
               // double ar[] = sensorDatalist.get(0).getDataList().get(0);
                //Log.d("Sensor data", ar.length+"");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("FileRead", "File not found or not a CSV file");
        }
        // create line chart
        createLineCharts();
    }

    private void createLineCharts() {
        LinearLayout chartContainer = view.findViewById(R.id.chartContainer);
        for (SensorData sensorData : sensorDatalist) {
            LineChart lineChart = createLineChart(sensorData);
            chartContainer.addView(lineChart);
        }
    }

    private LineChart createLineChart(SensorData sensorData) {
        LineChart lineChart = new LineChart(requireContext());
        // Customize the chart based on your requirements
        lineChart = configureLineChart(lineChart);
        // Add name of the chart
        Description description = new Description();
        description.setText(sensorData.getSensorName());
        lineChart.setDescription(description);

        // Create LineData based on the provided SensorData
        LineData lineData = createLineData(sensorData.getDataList(),lineChart);
        lineChart.setData(lineData);

        return lineChart;
    }

    private LineChart configureLineChart(LineChart lineChart) {

        lineChart.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(300) // Set the height in dp here
        ));
        // Enable or disable description text
        lineChart.getDescription().setEnabled(true);

        // Enable touch gestures
        lineChart.setTouchEnabled(true);

        // Enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);

        // If disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // Set an alternative background color
        lineChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // Add empty data
        lineChart.setData(data);

        // Get the legend (only possible after setting data)
        Legend legend = lineChart.getLegend();

        // Modify the legend ...
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.WHITE);

        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setDrawBorders(true);

        return lineChart;
    }


    private LineData createLineData(List<double[]> dataList, LineChart lineChart) {
        List<ILineDataSet> dataSets = new ArrayList<>();
        float minY = Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        double[] dArr = dataList.get(0);

        for(int j = 0; j<dArr.length; j++){
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                double[] dataArr = dataList.get(i);
                entries.add(new Entry(i, (float) dataArr[j]));
                // Determine the minimum and maximum values for all lines
                float value = (float) dataArr[j];
                    if (value < minY) {
                        minY = value;
                    }
                    if (value > maxY) {
                        maxY = value;
                    }
                }
            LineDataSet dataSet = new LineDataSet(entries, "DataSet " + j);
            //set color
            dataSet = setColor(dataSet);
            //add dataset
            dataSets.add(dataSet);
        }

        // Create and return the LineData
        LineData lineData = new LineData(dataSets);

        // Set the Y-axis range based on the overall minimum and maximum values
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(minY);
        leftAxis.setAxisMaximum(maxY);
        // After setting minY and maxY for the Y-axis
        leftAxis.setAxisMinimum(minY);
        leftAxis.setAxisMaximum(maxY);

// Add LimitLines for minY and maxY
        LimitLine minYLimitLine = new LimitLine(minY,  minY+"");
        LimitLine maxYLimitLine = new LimitLine(maxY, maxY+"");

// Set label positions to the left
        minYLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        maxYLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);

// Customize the appearance of the LimitLines if needed
        minYLimitLine.setLineColor(Color.RED);
        maxYLimitLine.setLineColor(Color.GREEN);

// Add the LimitLines to the Y-axis
        leftAxis.addLimitLine(minYLimitLine);
        leftAxis.addLimitLine(maxYLimitLine);

        return lineData;
    }

    private LineDataSet setColor(LineDataSet lineDataSet){
        Random random = new Random();
        int randomColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        lineDataSet.setColor(randomColor);
        lineDataSet.setCircleColor(randomColor);

        return lineDataSet;
    }

    private long convertTimestampToMillis(String timestamp) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            Date date = dateFormat.parse(timestamp);
            if (date != null) {
                return date.getTime(); // Convert the date to milliseconds
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
