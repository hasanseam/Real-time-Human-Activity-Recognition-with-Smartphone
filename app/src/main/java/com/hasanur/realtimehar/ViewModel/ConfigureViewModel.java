package com.hasanur.realtimehar.ViewModel;

import androidx.lifecycle.ViewModel;

public class ConfigureViewModel extends ViewModel {
    private int tabPosition;

    public ConfigureViewModel(){
        this.tabPosition = 0;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    public int getTabPosition() {
        return tabPosition;
    }
}
