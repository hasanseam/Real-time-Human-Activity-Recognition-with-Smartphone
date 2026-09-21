package com.hasanur.realtimehar.ViewModel;

import android.hardware.Sensor;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SensorConfigureViewModel extends ViewModel {

    private ArrayList<Sensor> checkedSensors;

    private ArrayList<Sensor> registeredSensors;

    public SensorConfigureViewModel(){
        checkedSensors  = new ArrayList<Sensor>();
        registeredSensors = new ArrayList<Sensor>();
    }

    public void addCheckedSensors(Sensor sensor){
        this.checkedSensors.add(sensor);
    }

    public void removeCheckedSensors(Sensor sensor){
        this.checkedSensors.remove(sensor);
    }

    public void addRegisteredSensors(Sensor sensor){this.registeredSensors.add(sensor);}

    public void removeRegistredSensors(){this.registeredSensors.clear();}

    public  ArrayList<Sensor> getRegisteredSensors(){return registeredSensors;}

    public ArrayList<Sensor> getCheckedSensors() {
        return checkedSensors;
    }

    private int selectedFrequencyUs = 20000; // default 50Hz (20000 us)

    public void setSelectedFrequencyUs(int frequency) {
        this.selectedFrequencyUs = frequency;
    }

    public int getSelectedFrequencyUs() {
        return selectedFrequencyUs;
    }
}
