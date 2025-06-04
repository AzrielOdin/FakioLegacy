package com.example.fakiolegacy.models;

import java.util.Date;

public class HistoryItem {
    private String filename;
    private String imagePath;
    private long timestamp;

    public HistoryItem(String filename, String imagePath) {
        this.filename = filename;
        this.imagePath = imagePath;
        this.timestamp = new Date().getTime();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}