package com.hasanur.realtimehar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hasanur.realtimehar.Adapter.ConfigureViewPageAdapter;

public class ConfigureFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2  viewPager2;
    private ConfigureViewPageAdapter configureViewPageAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_configure, container, false);

       tabLayout = fragmentView.findViewById(R.id.tabLayoutInConfigure);
       viewPager2 = fragmentView.findViewById(R.id.viewPagerInConfigure);

       configureViewPageAdapter = new ConfigureViewPageAdapter(requireActivity());

       viewPager2.setAdapter(configureViewPageAdapter);

       String[] titles = new String[]{"Activity","Sensors"};

       new TabLayoutMediator(tabLayout,viewPager2,((tab, position) -> {tab.setText(titles[position]);})).attach();
        // Inflate the layout for this fragment
        return fragmentView;
    }
}