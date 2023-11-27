package com.example.a123;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;



public class LocationHelper {
    private final int FINE_PERMISSION_CODE = 1;
    private final Activity context;
    private final FusedLocationProviderClient fusedLocationProviderClient;

    public LocationHelper(Activity context, FusedLocationProviderClient fusedLocationProviderClient) {
        this.context = context;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    public void getLastLocation(OnSuccessListener<Location> onSuccessListener) {
        if (context == null) {
            // Log an error or handle it appropriately
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(onSuccessListener);
    }

}
