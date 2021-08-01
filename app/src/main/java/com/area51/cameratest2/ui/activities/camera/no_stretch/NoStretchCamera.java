package com.area51.cameratest2.ui.activities.camera.no_stretch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.area51.cameratest2.R;
import com.area51.cameratest2.utils.ImageUtils;
import com.area51.cameratest2.utils.widgets.CameraPreview;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

import static com.area51.cameratest2.utils.ImageUtils.rotateImage;

public class NoStretchCamera extends AppCompatActivity
        implements Camera.PreviewCallback {

    private Camera mCamera;
    private FrameLayout cameraFrame;
    private AppCompatImageView mImageViewPreview;
    private ToggleButton toggleCamera;

    public static Intent getIntent(Context context) {
        return new Intent(context, NoStretchCamera.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_stretch_camera);

        cameraFrame = findViewById(R.id.cameraFrame);
        mImageViewPreview = findViewById(R.id.imageViewPreview);
        toggleCamera = findViewById(R.id.toggleCamera);
        mCamera = getCameraInstance();
        CameraPreview cameraPreview = new CameraPreview(NoStretchCamera.this, mCamera, this);
        cameraFrame.addView(cameraPreview);

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
            try {
                Camera.Parameters params = c.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                List<Camera.Size> sizes = params.getSupportedPictureSizes();

                Camera.Size mSize = sizes.get(sizes.size() / 2);

                for (Camera.Size size : sizes) {
                    if (size.height > mSize.height) {
                        mSize = size;
                    }
                }
                params.setPictureSize(mSize.width, mSize.height);
                c.setParameters(params);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {
        }
        return c;
    }

    private void saveImage(Bitmap bitmapImage) {
        File imageDir = new File(ImageUtils.getImageLocation());
        imageDir.mkdirs();
        String fileName = Calendar.getInstance(Locale.getDefault()).getTimeInMillis() + ".jpg";
        File file = new File(imageDir, fileName);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
            Glide.with(NoStretchCamera.this)
                    .load(file.getAbsolutePath())
                    .transition(new DrawableTransitionOptions()
                            .crossFade(300))
                    .into(mImageViewPreview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (toggleCamera.isChecked()) {
            Timber.i("onPreviewFrame: ");
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
//        saveImage(rotatedBitmap);
        }

    }

}