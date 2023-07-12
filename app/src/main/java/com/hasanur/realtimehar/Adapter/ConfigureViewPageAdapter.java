package com.hasanur.realtimehar.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hasanur.realtimehar.ActivityConfigureFragment;
import com.hasanur.realtimehar.SensorConfigureFragment;

import java.util.ArrayList;

public class ConfigureViewPageAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragmentArrayList = new ArrayList<Fragment>();

    public ConfigureViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        add(new ActivityConfigureFragment());
        add(new SensorConfigureFragment());


    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       /* switch (position){
            case 0 :
                return new ActivityConfigureFragment();
            case 1:
                return new SensorConfigureFragment();
        }*/
       // return new ActivityConfigureFragment();
        return fragmentArrayList.get(position);
    }


    @Override
    public int getItemCount() {
        return fragmentArrayList.size();
    }

    public void add(Fragment fragment){
        fragmentArrayList.add(fragment);
    }
}
