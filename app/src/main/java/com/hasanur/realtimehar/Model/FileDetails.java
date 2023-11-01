package com.hasanur.realtimehar.Model;

import java.io.File;

public class FileDetails {
    private String fileName;
    private String fileDateTime;
    private String fileSize;
    private File file;

    public FileDetails(String fileName, String fileDateTime, String fileSize, File file) {
        this.fileName = fileName;
        this.fileDateTime = fileDateTime;
        this.fileSize = fileSize;
        this.file = file;
    }

    // Getters for the fields
    public String getFileName() {
        return fileName;
    }

    public String getFileDateTime() {
        return fileDateTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public File getFile() {
        return file;
    }
}

