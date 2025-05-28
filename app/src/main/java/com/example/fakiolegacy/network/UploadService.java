package com.example.fakiolegacy.network;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadService {
    @Multipart
    @POST("upload")
    Call<UploadResponse> uploadImage(@Part MultipartBody.Part file);
}
