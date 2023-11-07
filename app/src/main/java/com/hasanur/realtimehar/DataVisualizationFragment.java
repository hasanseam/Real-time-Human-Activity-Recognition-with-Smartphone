package com.hasanur.realtimehar;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.hasanur.realtimehar.ViewModel.DataVisualizationViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataVisualizationFragment extends Fragment {

    private DataVisualizationViewModel dataVisualizationViewModel;
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

        fileName = dataVisualizationViewModel.getFilename();
        chartConfiguration();
        readDataFromFile(fileName);
        fileNameTextView.setText(fileName);

        return view;
    }

    private void chartConfiguration(){
        mChart = (LineChart) view.findViewById(R.id.lineChart);

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
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
    }


    private void readDataFromFile(String fileName) {
        File directory = new File(requireContext().getExternalFilesDir(null), "RealtimeHAR");
        File fileToRead = new File(directory, fileName);
        List<Entry> entries  = new ArrayList<>();

        float minY = Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        if (fileToRead.exists() && fileToRead.isFile() && fileToRead.getName().endsWith(".csv")) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead));
                String line;

                int index = 0; // Index for x-axis
                while ((line = bufferedReader.readLine()) != null) {
                    Log.d("Filedata", line);
                    String[] values = line.split(",");
                    if (values.length >= 5) {
                        long timestampInMillis = convertTimestampToMillis(values[0]);
                        float x = index++; // Increment x-axis index
                        float z = Float.parseFloat(values[2]); // Assuming Z-values are in the fourth column
                        entries.add(new Entry(x, z));

                        // Determine the minimum and maximum Y-values for setting the Y-axis range
                        if (z < minY) {
                            minY = z;
                        }
                        if (z > maxY) {
                            maxY = z;
                        }
                    }
                }

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("FileRead", "File not found or not a CSV file");
        }

        LineDataSet dataSet = new LineDataSet(entries, "Z-values");
        // ... (your previous LineDataSet configuration)
        LineData lineData = new LineData(dataSet);

        mChart.setData(lineData);

        // Set the Y-axis range based on the minimum and maximum values
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(minY);
        leftAxis.setAxisMaximum(maxY);

        mChart.invalidate(); // Refresh the chart to display the data

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
}
