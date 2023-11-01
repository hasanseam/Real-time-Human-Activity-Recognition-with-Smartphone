package com.hasanur.realtimehar;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

public class FileVisualizationFragment extends Fragment {
    private File selectedFile;

    public FileVisualizationFragment() {
        // Required empty public constructor
    }

    public static FileVisualizationFragment newInstance(File file) {
        FileVisualizationFragment fragment = new FileVisualizationFragment();
        fragment.selectedFile = file;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_visualization, container, false);

        // Access the views in your layout and populate with file data
        TextView fileNameTextView = view.findViewById(R.id.name);

        if (selectedFile != null) {
            fileNameTextView.setText(selectedFile.getName());

        }

        return view;
    }
}
