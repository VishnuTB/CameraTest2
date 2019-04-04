package com.area51.cameratest2.ui.activities.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.area51.cameratest2.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    public static final String SHOOT_CONTINUOUSLY = "shoot_continuously";
    public static final String SHOOT_SINGLE = "shoot_single";
    private static final String SHOOT_TYPE = "shoot_type";
    private static final String INTERVALS_IN_SECONDS = "intervals_in_seconds";
    private static final String TAG = CameraActivity.class.getSimpleName();

    private boolean shouldTakePhoto = false;
    private boolean shouldTakePhotoInIntervals = false;
    private int imageCount;
    private int imageLimit = 0;
    private int intervalsInSeconds = 0;
    private Camera mCamera;

    private Timer clickTimer;

    private AppCompatTextView mTextViewImageCount;
    private ToggleButton mToggleButtonCamera;
    private AppCompatImageView mImageViewPreview;

    public static Intent getIntent(Context context,
                                   String shoot_type,
                                   int intervals_in_seconds) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(SHOOT_TYPE, shoot_type);
        intent.putExtra(INTERVALS_IN_SECONDS, intervals_in_seconds);
        return intent;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        SurfaceView mSurfaceViewCamera = findViewById(R.id.surfaceViewCamera);
        mTextViewImageCount = findViewById(R.id.textViewImageCount);
        mToggleButtonCamera = findViewById(R.id.toggleCamera);
        mImageViewPreview = findViewById(R.id.imageViewPreview);

        SurfaceHolder mSurfaceHolderCamera = mSurfaceViewCamera.getHolder();
        mSurfaceHolderCamera.addCallback(this);
        mSurfaceHolderCamera.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mToggleButtonCamera.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.i(TAG, "onCreate: isChecked");
                if (intervalsInSeconds > 0) {
                    Log.i(TAG, "onCreate: isChecked - intervalsInSeconds > 0");
                    clickTimer = new Timer();
                    clickTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                Log.i(TAG, "onCreate: isChecked - intervalsInSeconds > 0 - runOnUiThread");
                                shouldTakePhotoInIntervals = true;
                                Toast.makeText(CameraActivity.this, "Taking picture", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }, 0, intervalsInSeconds);
                } else {
                    Log.w(TAG, "onCreate: isChecked - intervalsInSeconds < 0");
                    shouldTakePhoto = true;
                    shouldTakePhotoInIntervals = false;
                }
            } else {
                Log.e(TAG, "onCreate: isChecked");
                if (clickTimer != null) {
                    clickTimer.cancel();
                    clickTimer.purge();
                }
                shouldTakePhoto = false;
                shouldTakePhotoInIntervals = false;
            }
        });

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString(SHOOT_TYPE, "").equals(SHOOT_CONTINUOUSLY)) {
                intervalsInSeconds = (getIntent().getExtras().getInt(INTERVALS_IN_SECONDS, 0) * 1000);
                Log.i(TAG, "onCreate: intervalsInSeconds : " + intervalsInSeconds);
                imageLimit = 0;
            } else if (getIntent().getExtras().getString(SHOOT_TYPE, "").equals(SHOOT_SINGLE)) {
                imageLimit = 1;
                intervalsInSeconds = 0;
            }
        }

        mTextViewImageCount.setText(String.format(getString(R.string.placeholder_image_count), imageCount));

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.setPreviewCallback(null);
        if (clickTimer != null) {
            clickTimer.cancel();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (checkCameraHardware(this)) {
            startCamera();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
                mCamera.setDisplayOrientation(90);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
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

    private void startCamera() {
        mCamera = getCameraInstance();
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

            mCamera.setPreviewCallback((data, camera) -> {
                if (shouldTakePhoto || shouldTakePhotoInIntervals) {
                    Log.i(TAG, "startCamera: shouldTakePhoto : " + shouldTakePhoto);
                    shouldTakePhotoInIntervals = false;
                    imageCount++;
                    Log.i(TAG, "Photo count : " + imageCount);
                    Camera.Parameters cameraParameters = camera.getParameters();
                    int width = cameraParameters.getPreviewSize().width;
                    int height = cameraParameters.getPreviewSize().height;
                    int format = cameraParameters.getPreviewFormat();
                    YuvImage image = new YuvImage(data, format, width, height, null);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Rect area = new Rect(0, 0, width, height);
                    image.compressToJpeg(area, 50, out);
                    Bitmap imageBitMap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
                    Bitmap rotatedBitmap = rotateImage(imageBitMap, 90);
                    mTextViewImageCount.setText(String.format(getString(R.string.placeholder_image_count), imageCount));
                    if (imageCount == imageLimit) {
                        mToggleButtonCamera.setChecked(false);
                        imageCount = 0;
                    }
                    mImageViewPreview.setImageBitmap(rotatedBitmap);
                    useImageAt(rotatedBitmap);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*Images captured will be received here*/
    private void useImageAt(Bitmap rotatedBitmap) {
        
    }

}