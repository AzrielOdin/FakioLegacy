package com.example.fakiolegacy.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fakiolegacy.models.HistoryItem;
import com.example.fakiolegacy.repositories.HistoryRepository;

import java.util.List;

public class HistoryViewModel extends ViewModel {

    private final HistoryRepository repository;
    private final MutableLiveData<List<HistoryItem>> historyItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public HistoryViewModel(HistoryRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<HistoryItem>> getHistoryItems() {
        return historyItems;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadHistory() {
        isLoading.setValue(true);
        List<HistoryItem> items = repository.getHistory();
        historyItems.setValue(items);
        isLoading.setValue(false);
    }

    public void clearHistory() {
        repository.clearHistory();
        loadHistory();
    }
}