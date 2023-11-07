package com.hasanur.realtimehar.ViewModel;

import androidx.lifecycle.ViewModel;

public class DataVisualizationViewModel extends ViewModel {
    private String fileName;

    public DataVisualizationViewModel(){
        this.fileName = "";
    }
    public void setFileName(String name){
        this.fileName = name;
    }
    public String getFilename(){
        return this.fileName;
    }


}
