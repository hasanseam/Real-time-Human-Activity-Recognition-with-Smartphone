package com.hasanur.realtimehar;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.hasanur.realtimehar.Adapter.FileAdapter;
import com.hasanur.realtimehar.Interface.FileItemClickListener;
import com.hasanur.realtimehar.Model.FileDetails;
import com.hasanur.realtimehar.ViewModel.DataVisualizationViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        setupRecyclerView();
        return fragmentView;
    }

    private void deleteFile(File file) {
        // Check if the file exists
        if (file.exists()) {
            // Attempt to delete the file
            boolean deleted = file.delete();
            if (deleted) {
                // Notify the user that the file has been successfully deleted
                Toast.makeText(requireContext(), "File deleted successfully", Toast.LENGTH_SHORT).show();
                setupRecyclerView();
            } else {
                // Notify the user if an error occurs during the deletion process
                Toast.makeText(requireContext(), "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Notify the user if the file does not exist
            Toast.makeText(requireContext(), "File does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    private void startFileDownload(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = requireContext().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    OutputStream out = requireContext().getContentResolver().openOutputStream(uri);
                    InputStream in = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;
                    if (out != null) {
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                        out.close();
                    }
                    in.close();
                    Toast.makeText(requireContext(), "File downloaded successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error downloading file", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            String state = Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                Toast.makeText(requireContext(), "External storage not writable", Toast.LENGTH_SHORT).show();
                return;
            }

            File destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File destFile = new File(destDir, file.getName());

            try {
                InputStream in = new FileInputStream(file);
                OutputStream out = new FileOutputStream(destFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
                Toast.makeText(requireContext(), "File downloaded successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error downloading file", Toast.LENGTH_SHORT).show();
            }
        }

        Log.d("Kichuna", "Seam click korse download e");
    }

    private void setupRecyclerView() {
        File directory = new File(requireActivity().getExternalFilesDir(null), "RealtimeHAR");
        File[] files = directory.listFiles();
        List<FileDetails> fileDetailsList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".csv")) {
                    String fileName = file.getName();
                    String fileDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date(file.lastModified()));
                    long fileSize = file.length(); // in bytes
                    fileDetailsList.add(new FileDetails(fileName, fileDateTime, formatSize(fileSize), file));
                }
            }
        }

        FileAdapter fileAdapter = new FileAdapter(fileDetailsList, new FileItemClickListener() {
            @Override
            public void onItemClick(File file) {
                openAnotherFragment(file);
                // openFileFragment();
            }

            @Override
            public void onDownloadClick(File file) {
                // Implement file download logic here
                startFileDownload(file);
            }

            @Override
            public void onDeleteClick(File file) {
                deleteFile(file);
            }
        });

        recyclerView.setAdapter(fileAdapter);
    }

    private void openFileFragment() {
        Log.d("Kichuna", "Seam click korse");
    }

    private void openAnotherFragment(File file) {
        Log.d("Name", file.getName());
        dataVisualizationViewModel.setFileName(file.getName());
        DataVisualizationFragment dataVisualizationFragment = new DataVisualizationFragment();

        // Navigate to the FileVisualizationFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout_main_activity, dataVisualizationFragment) // R.id.fragment_container is the
                                                                                     // container in your activity
                                                                                     // layout
                .addToBackStack(null) // This adds the transaction to the back stack
                .commit();
    }

    private String formatSize(long size) {
        String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int unitIndex = 0;

        while (size > 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return size + " " + units[unitIndex];
    }
}
