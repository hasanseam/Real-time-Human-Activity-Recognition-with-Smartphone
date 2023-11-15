package com.hasanur.realtimehar.Model;

import java.util.ArrayList;
import java.util.List;

public class SensorData {
    private String sensorName;
    private List<double []> dataList;

    public SensorData(String sensorName){
        this.sensorName = sensorName;
        this.dataList = new ArrayList<>(); // Initialize the List
    }

    public void addData(double data[]){
        this.dataList.add(data);
    }

    public List<double []> getDataList(){
        return this.dataList;
    }

    public String getSensorName() {
        return this.sensorName;
    }
}
