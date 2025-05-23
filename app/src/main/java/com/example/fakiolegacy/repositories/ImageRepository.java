package com.example.fakiolegacy.repositories;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.fakiolegacy.models.FolderItem;
import com.example.fakiolegacy.models.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageRepository {
    private static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
    };
    private final ContentResolver contentResolver;

    public ImageRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public List<FolderItem> loadImageFolders() {
        Map<String, FolderItem> folderMap = new HashMap<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        try (Cursor cursor = contentResolver.query(
                uri, IMAGE_PROJECTION, null, null, null)) {

            if (cursor != null) {
                int pathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int nameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(pathColumnIndex);
                    String imageName = cursor.getString(nameColumnIndex);
                    long id = cursor.getLong(idColumnIndex);

                    // Create uri for the image
                    Uri imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    // Get parent folder path
                    File imageFile = new File(imagePath);
                    String folderPath = imageFile.getParent();
                    if (folderPath == null) continue;

                    // Get folder name
                    String folderName = new File(folderPath).getName();

                    // Add folder if it doesn't exist
                    if (!folderMap.containsKey(folderPath)) {
                        folderMap.put(folderPath, new FolderItem(folderPath, folderName));
                    }

                    // Add image to folder
                    ImageItem image = new ImageItem(imagePath, imageName, imageUri);
                    folderMap.get(folderPath).getImages().add(image);
                }
            }
        }

        return new ArrayList<>(folderMap.values());
    }
}