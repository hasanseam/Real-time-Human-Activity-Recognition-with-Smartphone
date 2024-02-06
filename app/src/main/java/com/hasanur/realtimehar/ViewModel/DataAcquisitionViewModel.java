package com.hasanur.realtimehar.ViewModel;

import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

public class DataAcquisitionViewModel extends ViewModel {
    private boolean isListening;

    private List<LineChart> chartList ;

    public DataAcquisitionViewModel(){
        chartList = new ArrayList<>();
    }

    public void setChartList(LineChart chart) {
        this.chartList.add(chart);
    }

    public List<LineChart> getChartList() {
        return chartList;
    }

    public void resetChartList(){this.chartList.clear();}

    public void setListening(boolean value){
        isListening = value;
    }
    public boolean getListening(){
        return isListening;
    }
}
