package com.area51.cameratest2.ui.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;

import com.area51.cameratest2.R;
import com.area51.cameratest2.ui.activities.camera.CameraActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import static com.area51.cameratest2.ui.activities.camera.CameraActivity.SHOOT_CONTINUOUSLY;
import static com.area51.cameratest2.ui.activities.camera.CameraActivity.SHOOT_SINGLE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatEditText editTextIntervals = findViewById(R.id.editTextIntervals);

        findViewById(R.id.buttonContinuousShot)
                .setOnClickListener(v -> startActivity(CameraActivity.getIntent(MainActivity.this, SHOOT_CONTINUOUSLY, 0)));

        findViewById(R.id.buttonSingleShot)
                .setOnClickListener(v -> startActivity(CameraActivity.getIntent(MainActivity.this, SHOOT_SINGLE, 0)));

        findViewById(R.id.buttonIntervalsShot)
                .setOnClickListener(v -> {
                    int interval = (editTextIntervals.getText().toString().length() > 0) ? Integer.parseInt(editTextIntervals.getText().toString()) : 0;
                    startActivity(CameraActivity.getIntent(MainActivity.this, SHOOT_CONTINUOUSLY, interval));
                });

        requestCameraPermission();

    }

    private void requestCameraPermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        requestReadSDCardPermission();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        requestCameraPermission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    private void requestReadSDCardPermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        requestWriteSDCardPermission();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        requestReadSDCardPermission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    private void requestWriteSDCardPermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        requestWriteSDCardPermission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

}