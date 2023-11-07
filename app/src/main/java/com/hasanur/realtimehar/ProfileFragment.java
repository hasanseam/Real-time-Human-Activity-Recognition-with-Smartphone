package com.hasanur.realtimehar;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hasanur.realtimehar.Adapter.FileAdapter;
import com.hasanur.realtimehar.Interface.FileItemClickListener;
import com.hasanur.realtimehar.Model.FileDetails;
import com.hasanur.realtimehar.ViewModel.DataVisualizationViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    RecyclerView recyclerView;

    private DataVisualizationViewModel dataVisualizationViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        dataVisualizationViewModel = new ViewModelProvider(requireActivity()).get(DataVisualizationViewModel.class);

        recyclerView = fragmentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        File directory = new File(requireActivity().getExternalFilesDir(null), "RealtimeHAR");
        File[] files = directory.listFiles();
        List<FileDetails> fileDetailsList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".csv")) {
                    String fileName = file.getName();
                    String fileDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(file.lastModified()));
                    long fileSize = file.length(); // in bytes

                    fileDetailsList.add(new FileDetails(fileName, fileDateTime, formatSize(fileSize), file));
                }
            }
        }

        FileAdapter fileAdapter = new FileAdapter(fileDetailsList, new FileItemClickListener() {
            @Override
            public void onItemClick(File file) {
                // Open another fragment and perform action on file selection
                openAnotherFragment(file);
            }
        });

        recyclerView.setAdapter(fileAdapter);
        return fragmentView;
    }

    private void openAnotherFragment(File file) {
        Log.d("Name",file.getName());
        dataVisualizationViewModel.setFileName(file.getName());
        DataVisualizationFragment dataVisualizationFragment = new DataVisualizationFragment();

        // Navigate to the FileVisualizationFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_main_activity, dataVisualizationFragment) // R.id.fragment_container is the container in your activity layout
                .addToBackStack(null) // This adds the transaction to the back stack
                .commit();
    }

    private String formatSize(long size) {
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;

        while (size > 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return size + " " + units[unitIndex];
    }
}
