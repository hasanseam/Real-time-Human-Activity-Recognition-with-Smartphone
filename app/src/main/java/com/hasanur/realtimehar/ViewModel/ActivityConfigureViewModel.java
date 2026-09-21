package com.hasanur.realtimehar.ViewModel;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ActivityConfigureViewModel extends ViewModel {
    private ArrayList<String> activities;
    private ArrayList<String> activitiesRowNoInDatabase;
    private int selectedPosition;



    public ActivityConfigureViewModel() {
        this.activities  = new ArrayList<String>();
        this.activitiesRowNoInDatabase = new ArrayList<>();
        this.selectedPosition = -1;
    }

    public void addAcitivity(String rowNo,String activity){
        this.activities.add(activity);
        this.activitiesRowNoInDatabase.add(rowNo);
    }

    public void removeActivity(int position){
        if(position==selectedPosition){
            this.setSelectedPosition(-1);
            
        }
        this.activities.remove(position);
    }

    public String getActivitiesRowNoInDatabase (int position){
        return this.activitiesRowNoInDatabase.get(position);
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

    private String subjectId = "";

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectId() {
        return subjectId;
    }
}
