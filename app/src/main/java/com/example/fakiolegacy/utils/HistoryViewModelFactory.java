package com.example.fakiolegacy.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.fakiolegacy.repositories.HistoryRepository;
import com.example.fakiolegacy.ui.history.HistoryViewModel;

public class HistoryViewModelFactory implements ViewModelProvider.Factory {

    private final HistoryRepository repository;

    public HistoryViewModelFactory(HistoryRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HistoryViewModel.class)) {
            return (T) new HistoryViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}