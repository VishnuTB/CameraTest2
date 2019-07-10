package com.area51.cameratest2.ui.activities.camera.camera2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.area51.cameratest2.R;

public class Camera2Activity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, Camera2Activity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.camera2Frame, Camera2BasicFragment.newInstance())
                .commit();
    }

}