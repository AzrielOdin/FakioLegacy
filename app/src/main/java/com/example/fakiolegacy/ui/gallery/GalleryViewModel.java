package com.example.fakiolegacy.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fakiolegacy.models.FolderItem;
import com.example.fakiolegacy.models.ImageItem;
import com.example.fakiolegacy.repositories.ImageRepository;

import java.util.List;

public class GalleryViewModel extends ViewModel {
    private final ImageRepository repository;
    private final MutableLiveData<List<FolderItem>> folders = new MutableLiveData<>();
    private final MutableLiveData<FolderItem> selectedFolder = new MutableLiveData<>();
    private final MutableLiveData<ImageItem> selectedImage = new MutableLiveData<>();

    public GalleryViewModel(ImageRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<FolderItem>> getFolders() {
        return folders;
    }

    public LiveData<FolderItem> getSelectedFolder() {
        return selectedFolder;
    }

    public LiveData<ImageItem> getSelectedImage() {
        return selectedImage;
    }

    public void loadFolders() {
        new Thread(() -> {
            List<FolderItem> folderList = repository.loadImageFolders();
            folders.postValue(folderList);
            if (!folderList.isEmpty()) {
                selectedFolder.postValue(folderList.get(0));
                if (!folderList.get(0).getImages().isEmpty()) {
                    selectedImage.postValue(folderList.get(0).getImages().get(0));
                }
            }
        }).start();
    }

    public void selectFolder(FolderItem folder) {
        selectedFolder.setValue(folder);
        if (!folder.getImages().isEmpty()) {
            selectedImage.setValue(folder.getImages().get(0));
        } else {
            selectedImage.setValue(null);
        }
    }

    public void selectImage(ImageItem image) {
        selectedImage.setValue(image);
    }
}