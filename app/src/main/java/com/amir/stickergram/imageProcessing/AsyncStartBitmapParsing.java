package com.amir.stickergram.imageProcessing;

import android.graphics.Color;
import android.os.AsyncTask;

/**
 * remove all the transparency from the bitmap and return the location and their transparency
 * for recreation of the bitmap
 * <p>
 * this is because the normal bitmap parsing would remove
 * the color information of the transparent pixels
 */

public class AsyncStartBitmapParsing extends AsyncTask<Void, Void, Void> {
    private int[] pixels;
    private int[] pixelTransparency;
    private AsyncStartBitmapProcessingCallbacks listener;

    AsyncStartBitmapParsing(int[] pixels , AsyncStartBitmapProcessingCallbacks listener) {
        this.pixels = pixels;
        this.listener  = listener;
        pixelTransparency = new int[pixels.length];
//        transparentPixelsLocation = new int[pixels.length];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.opacingStarted();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int size = pixels.length;
        int mPixel;
        for (int i = 0; i < size; i++) {
            pixelTransparency[i] = Color.alpha(pixels[i]);
            mPixel = pixels[i];
            pixels[i] = Color.argb(255, Color.red(mPixel), Color.green(mPixel), Color.blue(mPixel));

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        int[][] mValues = new int[2][];
        mValues[0] = pixels;
        mValues[1] = pixelTransparency;
        listener.opacingFinished(mValues);
    }

    interface AsyncStartBitmapProcessingCallbacks {
        void opacingStarted();

        void opacingFinished(int[][] pixels);
    }
}
