package com.example.fakiolegacy.models;

import java.util.ArrayList;
import java.util.List;

public class FolderItem {
    private final String path;
    private final String name;
    private List<ImageItem> images;

    // Constructor, getters and setters
    public FolderItem(String path, String name) {
        this.path = path;
        this.name = name;
        this.images = new ArrayList<>();
    }

    // Getters and setters
    public String getPath() { return path; }
    public String getName() { return name; }
    public List<ImageItem> getImages() { return images; }
    public void setImages(List<ImageItem> images) { this.images = images; }
}
