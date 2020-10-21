package com.area51.cameratest2.ui.activities.camera.appbar_camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.area51.cameratest2.R;
import com.area51.cameratest2.utils.listeners.AppBarStateChangeListener;
import com.area51.cameratest2.utils.widgets.CameraPreview;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

public class AppBarCamera extends AppCompatActivity
        implements Camera.PreviewCallback {

    boolean takePhoto = false;
    private FrameLayout cameraFrame;
    private final String TAG = AppBarCamera.class.getSimpleName();
    private Camera mCamera;
    private FloatingActionButton mButtonCapture;

    public static Intent getIntent(Context context) {
        return new Intent(context, AppBarCamera.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_bar_camera);

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        cameraFrame = findViewById(R.id.cameraFrame);
        mButtonCapture = findViewById(R.id.fabCapture);

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State currentState) {
                Timber.d(currentState.name());
                switch (currentState) {
                    case IDLE:
                    case COLLAPSED:
                        mButtonCapture.hide();
                        takePhoto = false;
                        break;
                    case EXPANDED:
                        mButtonCapture.show();
                        takePhoto = true;
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
        CameraPreview cameraPreview = new CameraPreview(AppBarCamera.this, mCamera, this);
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
//            Camera.Parameters cameraParameters = camera.getParameters();
//            int width = cameraParameters.getPreviewSize().width;
//            int height = cameraParameters.getPreviewSize().height;
//            int format = cameraParameters.getPreviewFormat();
//            YuvImage image = new YuvImage(data, format, width, height, null);
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            Rect area = new Rect(0, 0, width, height);
//            image.compressToJpeg(area, 50, out);
//            Bitmap imageBitMap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
//            Bitmap rotatedBitmap = rotateImage(imageBitMap, 90);
        }
    }

}