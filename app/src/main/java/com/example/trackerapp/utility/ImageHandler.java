package com.example.trackerapp.utility;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageHandler {
    public Bitmap getResizedBitmap(Bitmap bitmap, int newHeight, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    public Bitmap getResizedBitmapForPoster(Bitmap bitmap) {
        Bitmap resizedBitmap = getResizedBitmap(bitmap, 200, 150);
        return resizedBitmap;
    }
}
