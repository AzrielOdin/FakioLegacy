package com.example.fakiolegacy.models;

import android.net.Uri;

public class ImageItem {
    private final String path;
    private final String name;
    private final Uri uri;
    private final long dateModified;

    public ImageItem(String path, String name, Uri uri, long dateModified) {
        this.path = path;
        this.name = name;
        this.uri = uri;
        this.dateModified = dateModified;
    }

    public String getPath() { return path; }
    public String getName() { return name; }
    public Uri getUri() { return uri; }
    public long getDateModified() { return dateModified; }
}