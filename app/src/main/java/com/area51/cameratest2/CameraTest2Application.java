package com.area51.cameratest2;

import android.app.Application;

import timber.log.Timber;

public class CameraTest2Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

}