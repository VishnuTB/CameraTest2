package com.area51.cameratest2.ui.activities.camera.appbar_camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.area51.cameratest2.R;
import com.area51.cameratest2.utils.listeners.AppBarStateChangeListener;
import com.area51.cameratest2.utils.widgets.CameraPreview;

import java.util.List;

import timber.log.Timber;

public class AppBarCamera extends AppCompatActivity
        implements Camera.PreviewCallback {

    boolean takePhoto = false;
    private FrameLayout cameraFrame;
    private Camera mCamera;
    private CameraPreview cameraPreview;
    private AppBarLayout appBarLayout;
    private String TAG = AppBarCamera.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, AppBarCamera.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_bar_camera);

        appBarLayout = findViewById(R.id.appBarLayout);
        cameraFrame = findViewById(R.id.cameraFrame);

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State currentState) {
                Timber.d(currentState.name());
                switch (currentState) {
                    case IDLE:
                        takePhoto = false;
                        break;
                    case EXPANDED:
                        takePhoto = true;
                        break;
                    case COLLAPSED:
                        takePhoto = false;
                        break;
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    private void startCamera() {
        mCamera = getCameraInstance();
        cameraPreview = new CameraPreview(AppBarCamera.this, mCamera, this);
        cameraFrame.addView(cameraPreview);
        try {
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            List<Camera.Size> sizes = params.getSupportedPictureSizes();

            Camera.Size mSize = sizes.get(sizes.size() / 2);

            for (Camera.Size size : sizes) {
                if (size.height > mSize.height) {
                    mSize = size;
                }
            }
            params.setPictureSize(mSize.width, mSize.height);
            mCamera.setParameters(params);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.setPreviewCallback(null);
    }

    private Camera getCameraInstance() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    10001);
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception ignored) {
        }
        return c;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (takePhoto) {
            Timber.i("onPreviewFrame: ");
        }
    }

}