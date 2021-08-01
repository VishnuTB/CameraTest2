package com.area51.cameratest2.ui.activities.camera.camerax;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.area51.cameratest2.R;
import com.area51.cameratest2.utils.ImageUtils;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

public class CameraXActivity extends AppCompatActivity {

    private static final String KEY_FOLDER_NAME = "key.folder_name";
    private PreviewView mPreviewView;
    private AppCompatImageButton captureImage;
    private AppCompatTextView mTextViewImageCount;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private String folderName;
    private int imageCount = 0;
    private Camera camera;

    public static Intent getIntent(Context context, String folderName) {
        Intent intent = new Intent(context, CameraXActivity.class);
        intent.putExtra(KEY_FOLDER_NAME, folderName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);

        mPreviewView = findViewById(R.id.previewView);
        mTextViewImageCount = findViewById(R.id.textViewImageCount);
        captureImage = findViewById(R.id.captureImage);

        if (Objects.requireNonNull(getIntent().getExtras()).containsKey(KEY_FOLDER_NAME)) {
            folderName = getIntent().getExtras().getString(KEY_FOLDER_NAME, "default");
            Timber.i(folderName);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
        mTextViewImageCount.setText(String.format(getString(R.string.placeholder_image_count), imageCount));

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);

        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        captureImage.setOnClickListener(v -> {
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(ImageUtils.getImageFile(folderName))
                    .build();
            imageCapture.takePicture(outputFileOptions,
                    executor,
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            updateImageCount();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException error) {
                            error.printStackTrace();
                        }
                    });
        });

    }

    private void updateImageCount() {
        runOnUiThread(() -> {
            imageCount++;
            mTextViewImageCount.setText(String.format(getString(R.string.placeholder_image_count), imageCount));
        });
    }

}