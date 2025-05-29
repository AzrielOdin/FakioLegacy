package com.example.fakiolegacy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class PermissionsHandler {
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final Map<Integer, PermissionCallback> callbackMap = new HashMap<>();

    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    public static boolean hasStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static void requestStoragePermission(Activity activity, PermissionCallback callback) {
        callbackMap.put(STORAGE_PERMISSION_CODE, callback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                    STORAGE_PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    public static void requestStoragePermission(Fragment fragment, PermissionCallback callback) {
        callbackMap.put(STORAGE_PERMISSION_CODE, callback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            fragment.requestPermissions(
                    new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                    STORAGE_PERMISSION_CODE);
        } else {
            fragment.requestPermissions(
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    public static boolean handlePermissionResult(int requestCode, @NonNull int[] grantResults) {
        if (callbackMap.containsKey(requestCode)) {
            PermissionCallback callback = callbackMap.get(requestCode);

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (callback != null) {
                    callback.onPermissionGranted();
                }
                return true;
            } else {
                if (callback != null) {
                    callback.onPermissionDenied();
                }
                return false;
            }
        }
        return false;
    }

    public static void showPermissionDeniedMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}