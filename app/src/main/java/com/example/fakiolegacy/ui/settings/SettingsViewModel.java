package com.example.fakiolegacy.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<Boolean> wifiOnlyEnabled;
    private final MutableLiveData<String> appVersion;

    public SettingsViewModel() {
        wifiOnlyEnabled = new MutableLiveData<>();
        wifiOnlyEnabled.setValue(true); // Default value

        appVersion = new MutableLiveData<>();
        appVersion.setValue("1.0.0"); // Default version
    }

    public LiveData<Boolean> getWifiOnlyEnabled() {
        return wifiOnlyEnabled;
    }

    public void setWifiOnlyEnabled(Boolean enabled) {
        wifiOnlyEnabled.setValue(enabled);
        // In a real app, you would save this to SharedPreferences or a repository
    }

    public LiveData<String> getAppVersion() {
        return appVersion;
    }
}