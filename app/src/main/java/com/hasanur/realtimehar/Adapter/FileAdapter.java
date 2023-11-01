package com.hasanur.realtimehar.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hasanur.realtimehar.Interface.FileItemClickListener;
import com.hasanur.realtimehar.Model.FileDetails;
import com.hasanur.realtimehar.R;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private List<FileDetails> fileDetailsList;
    private FileItemClickListener listener;

    public FileAdapter(List<FileDetails> fileDetailsList, FileItemClickListener listener) {
        this.fileDetailsList = fileDetailsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_file_details, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileDetails fileDetails = fileDetailsList.get(position);
        holder.bind(fileDetails, listener);
    }

    @Override
    public int getItemCount() {
        return fileDetailsList.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;
        TextView fileDateTimeTextView;
        TextView fileSizeTextView;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.textFileName);
            fileDateTimeTextView = itemView.findViewById(R.id.textFileDate);
            fileSizeTextView = itemView.findViewById(R.id.textFileSize);
        }

        public void bind(final FileDetails fileDetails, final FileItemClickListener listener) {
            fileNameTextView.setText(fileDetails.getFileName());
            fileDateTimeTextView.setText("Date: " + fileDetails.getFileDateTime());
            fileSizeTextView.setText("Size: " + fileDetails.getFileSize());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(fileDetails.getFile());
                }
            });
        }
    }
}
