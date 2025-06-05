package com.example.fakiolegacy.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {
    private final ContentResolver contentResolver;
    private final Context context;

    public FileHelper(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public File uriToFile(Uri uri) throws IOException {
        String fileName = getFileName(uri);

        if (fileName == null) {
            fileName = "temp_" + System.currentTimeMillis() + ".jpg";
        }

        File destinationFile = new File(context.getCacheDir(), fileName);

        try (InputStream inputStream = contentResolver.openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {

            if (inputStream == null) {
                throw new IOException("Failed to open input stream");
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            return destinationFile;
        }
    }

    public String getFileName(Uri uri) {
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }

        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);
        cursor.close();
        return name;
    }

    public String getMimeType(Uri uri) {
        return contentResolver.getType(uri);
    }
}