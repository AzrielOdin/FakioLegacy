package com.example.fakiolegacy.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.fakiolegacy.repositories.ImageRepository;
import com.example.fakiolegacy.ui.gallery.GalleryViewModel;

public class GalleryViewModelFactory implements ViewModelProvider.Factory {
    private final ImageRepository repository;

    public GalleryViewModelFactory(ImageRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GalleryViewModel.class)) {
            return (T) new GalleryViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
