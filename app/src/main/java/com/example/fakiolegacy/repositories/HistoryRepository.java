package com.example.fakiolegacy.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.fakiolegacy.models.HistoryItem;
import com.example.fakiolegacy.network.UploadResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryRepository {
    private static final String PREF_NAME = "image_history";
    private static final String KEY_HISTORY = "upload_history";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public HistoryRepository(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void addToHistory(String filename, String imagePath) {
        List<HistoryItem> historyItems = getHistory();
        historyItems.add(0, new HistoryItem(filename, imagePath));
        saveHistory(historyItems);
    }

    public void addToHistory(UploadResponse response, Uri imageUri) {
        if (response != null && response.getFilename() != null) {
            addToHistory(response.getFilename(), imageUri.toString());
        }
    }

    public List<HistoryItem> getHistory() {
        String json = sharedPreferences.getString(KEY_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<HistoryItem>>() {}.getType();
        List<HistoryItem> items = gson.fromJson(json, type);
        return items != null ? items : new ArrayList<>();
    }

    private void saveHistory(List<HistoryItem> historyItems) {
        if (historyItems.size() > 100) {
            historyItems = historyItems.subList(0, 100);
        }

        String json = gson.toJson(historyItems);
        sharedPreferences.edit().putString(KEY_HISTORY, json).apply();
    }

    public void clearHistory() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply();
    }
}