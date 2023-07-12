package com.hasanur.realtimehar.ViewModel;

import androidx.lifecycle.ViewModel;

public class DataAcquisitionViewModel extends ViewModel {
    private boolean isListening;

    public void setListening(boolean value){
        isListening = value;
    }
    public boolean getListening(){
        return isListening;
    }
}
