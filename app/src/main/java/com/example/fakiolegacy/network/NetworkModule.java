package com.example.fakiolegacy.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class NetworkModule {
    private static final String BASE_URL = "http://192.168.87.166:5000/";
    //TODO timout logic and retrty logic improvements
    private static final long TIMEOUT = 30;

    private static OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    private static Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

   //TODO see if this is added here correctly (RETROFIRT)
    public static UploadService provideUploadService() {
        OkHttpClient client = provideOkHttpClient();
        Retrofit retrofit = provideRetrofit(client);
        return retrofit.create(UploadService.class);
    }
}
