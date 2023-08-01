package com.hasanur.realtimehar.ViewModel;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ActivityConfigureViewModel extends ViewModel {
    private ArrayList<String> activities;
    private int selectedPosition;

    public ActivityConfigureViewModel() {
        this.activities  = new ArrayList<String>();
        this.selectedPosition = -1;
    }

    public void addAcitivity(String activity){
        this.activities.add(activity);
    }

    public void removeActivity(int position){
        if(position==selectedPosition){
            this.setSelectedPosition(-1);
            
        }
        this.activities.remove(position);
    }

    public ArrayList<String> getActivities(){
       return this.activities;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public String getSelectedActivities(){
        if(this.selectedPosition == -1){
            return null;
        }
        return this.activities.get(this.selectedPosition);
    }

}
