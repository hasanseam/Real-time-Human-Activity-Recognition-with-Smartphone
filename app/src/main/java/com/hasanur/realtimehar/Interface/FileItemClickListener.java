package com.hasanur.realtimehar.Interface;

import java.io.File;

public interface FileItemClickListener {
    void onItemClick(File file);
    void onDownloadClick(File file);
    void onDeleteClick(File file);

}
