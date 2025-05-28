package com.example.fakiolegacy.network;

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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class UploadRepository {
    private final UploadService uploadService;
    //TODO network should Repo know context?
    private final Context context;

    public UploadRepository(Context context) {
        this.context = context;
        this.uploadService = NetworkModule.provideUploadService();
    }

    public File uriToFile(Uri uri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        String fileName = getFileName(contentResolver, uri);

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

    private String getFileName(ContentResolver resolver, Uri uri) {
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }

        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);
        cursor.close();
        return name;
    }

    public void uploadImage(Uri imageUri, Callback<UploadResponse> callback) {
        try {
            File file = uriToFile(imageUri);

            RequestBody requestFile = RequestBody.create(
                MediaType.parse(context.getContentResolver().getType(imageUri)),
                file
            );

            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                requestFile
            );

            uploadService.uploadImage(filePart).enqueue(callback);

        } catch (IOException e) {
            callback.onFailure(null, e);
        }
    }
}