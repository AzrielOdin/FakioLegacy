package com.example.fakiolegacy.models;

import android.net.Uri;

public class ImageItem {
    private final String path;
    private final String name;
    private final Uri uri;

    // Constructor, getters and setters
    public ImageItem(String path, String name, Uri uri) {
        this.path = path;
        this.name = name;
        this.uri = uri;
    }

    // Getters and setters
    public String getPath() { return path; }
    public String getName() { return name; }
    public Uri getUri() { return uri; }
}