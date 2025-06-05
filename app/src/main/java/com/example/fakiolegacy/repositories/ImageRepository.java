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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ImageRepository {
    private static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_MODIFIED
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
                int dateModifiedColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);

                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(pathColumnIndex);
                    String imageName = cursor.getString(nameColumnIndex);
                    long id = cursor.getLong(idColumnIndex);
                    long dateModified = (dateModifiedColumnIndex != -1) ?
                                       cursor.getLong(dateModifiedColumnIndex) : 0;

                    Uri imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    File imageFile = new File(imagePath);
                    String folderPath = imageFile.getParent();
                    if (folderPath == null) continue;

                    String folderName = new File(folderPath).getName();

                    if (!folderMap.containsKey(folderPath)) {
                        folderMap.put(folderPath, new FolderItem(folderPath, folderName));
                    }

                    ImageItem image = new ImageItem(imagePath, imageName, imageUri, dateModified);
                    Objects.requireNonNull(folderMap.get(folderPath)).getImages().add(image);
                }
            }
        }

        for (FolderItem folder : folderMap.values()) {
            folder.getImages().sort((img1, img2) ->
                    Long.compare(img2.getDateModified(), img1.getDateModified()));
        }
        return new ArrayList<>(folderMap.values());
    }
}