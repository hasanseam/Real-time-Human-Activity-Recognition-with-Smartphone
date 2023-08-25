package com.hasanur.realtimehar.ViewModel;

import android.hardware.Sensor;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SensorConfigureViewModel extends ViewModel {

    private ArrayList<Sensor> checkedSensors;

    public SensorConfigureViewModel(){
        checkedSensors  = new ArrayList<Sensor>();
    }

    public void addCheckedSensors(Sensor sensor){
        this.checkedSensors.add(sensor);
    }

    public void removeCheckedSensors(Sensor sensor){
        this.checkedSensors.remove(sensor);
    }

    public ArrayList<Sensor> getCheckedSensors() {
        return checkedSensors;
    }
}
