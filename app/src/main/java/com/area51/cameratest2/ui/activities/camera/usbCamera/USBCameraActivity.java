package com.area51.cameratest2.ui.activities.camera.usbCamera;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import com.area51.cameratest2.R;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class USBCameraActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, USBCameraActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_s_b_camera);

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId :
                    cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer cameraLensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                Timber.i("Camera Lens Facing : " + cameraLensFacing);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

}