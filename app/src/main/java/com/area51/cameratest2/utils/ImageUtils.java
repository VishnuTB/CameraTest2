package com.area51.cameratest2.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class ImageUtils {

    public static String getImageLocation() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/CameraTest2";
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static File getImageFile(String folderName) {
        File imageDir = new File(getImageLocation() + "/" + folderName);
        imageDir.mkdirs();
        String fileName = Calendar.getInstance(Locale.getDefault()).getTimeInMillis() + ".jpg";
        File file = new File(imageDir, fileName);
        return file;
    }

}