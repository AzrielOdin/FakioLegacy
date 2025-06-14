package com.example.fakiolegacy;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.example.fakiolegacy.utils.PermissionsHandler;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fakiolegacy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!checkPermissions()) {
            requestPermissions();
        }

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_gallery, R.id.nav_history, R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private boolean checkPermissions() {
        return PermissionsHandler.hasStoragePermission(this);
    }

    private void requestPermissions() {
        PermissionsHandler.requestStoragePermission(this, new PermissionsHandler.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                //TODO refresh logic
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(MainActivity.this, "Image access permission is required to view gallery",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    //TODO Permissions in a handler file
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsHandler.handlePermissionResult(requestCode, grantResults);
    }
}