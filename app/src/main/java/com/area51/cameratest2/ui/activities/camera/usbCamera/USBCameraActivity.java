package com.area51.cameratest2.ui.activities.camera.usbCamera;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.view.Surface;

import com.area51.cameratest2.R;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.widget.CameraViewInterface;
import com.serenegiant.usb.widget.UVCCameraTextureView;

import androidx.appcompat.app.AppCompatActivity;

public class USBCameraActivity extends AppCompatActivity {

    private UVCCameraTextureView mUVCCameraView;
    private UVCCameraHelper mCameraHelper;
    private boolean isPreview;
    private boolean isRequest;
    private CameraViewInterface.Callback mCallback = new CameraViewInterface.Callback() {

        @Override
        public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
            // must have
            if (!isPreview && mCameraHelper.isCameraOpened()) {
                mCameraHelper.startPreview(mUVCCameraView);
                isPreview = true;
            }
        }

        @Override
        public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
            // must have
            if (isPreview && mCameraHelper.isCameraOpened()) {
                mCameraHelper.stopPreview();
                isPreview = false;
            }
        }

        @Override
        public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

        }
    };
    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            // request open permission(must have)
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera(must have)
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {

        }

        @Override
        public void onDisConnectDev(UsbDevice device) {

        }
    };

    public static Intent getIntent(Context context) {
        return new Intent(context, USBCameraActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_s_b_camera);

        mUVCCameraView = findViewById(R.id.textureViewUSB);
        mUVCCameraView.setCallback(mCallback);
        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
    }

}