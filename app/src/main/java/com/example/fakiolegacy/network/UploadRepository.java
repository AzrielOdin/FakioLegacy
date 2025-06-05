package com.example.fakiolegacy.network;

import android.net.Uri;

import com.example.fakiolegacy.utils.FileHelper;
import com.example.fakiolegacy.utils.Logger;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class UploadRepository {
    private final UploadService uploadService;
    private final FileHelper fileHelper;

    public UploadRepository(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
        this.uploadService = NetworkModule.provideUploadService();
    }

    public void uploadImage(Uri imageUri, Callback<UploadResponse> callback) {
        try {
            File file = fileHelper.uriToFile(imageUri);
            MediaType mediaType = MediaType.parse(fileHelper.getMimeType(imageUri));

            RequestBody requestFile = RequestBody.create(mediaType, file);

            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                requestFile
            );

            uploadService.uploadImage(filePart).enqueue(callback);

        } catch (IOException e) {
            // dummy call object to avoid passing null to a nonnull annotated parameter
            Logger.logError("Failed to convert URI to file", e);
            retrofit2.Call<UploadResponse> failedCall = uploadService.uploadImage(
                    MultipartBody.Part.createFormData("error", "error")
            );
            callback.onFailure(failedCall, e);
        }
    }
}